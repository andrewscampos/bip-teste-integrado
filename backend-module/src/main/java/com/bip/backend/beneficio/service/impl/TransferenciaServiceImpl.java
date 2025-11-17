package com.bip.backend.beneficio.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bip.backend.beneficio.dto.request.TransferenciaRequest;
import com.bip.backend.beneficio.service.TransferenciaService;
import com.bip.ejb.model.TransferenciaLog;
import com.bip.ejb.model.TransferenciaStatus;
import com.bip.ejb.service.BeneficioEjbService;
import com.bip.ejb.service.TransferenciaEjbService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransferenciaServiceImpl implements TransferenciaService {


    private final BeneficioEjbService ejbClient;
    private final TransferenciaEjbService transferenciaEjbService;

	public TransferenciaServiceImpl(BeneficioEjbService ejbClient, TransferenciaEjbService transferenciaEjbService) {
		this.ejbClient = ejbClient;
		this.transferenciaEjbService = transferenciaEjbService;
	}

    @Override
    public void executarTransferencia(TransferenciaRequest request) {
        String idempotencyKey = request.getIdempotencyKey();

        log.info("[TRANSFERENCIA] Iniciando: FROM={}, TO={}, AMOUNT={}, KEY={}",
                request.getFromId(), request.getToId(), request.getAmount(), idempotencyKey);

        Optional<TransferenciaLog> existingLog = transferenciaEjbService.findByIdempotencyKey(idempotencyKey);

        if (existingLog.isPresent()) {
            TransferenciaLog transferenciaLog = existingLog.get();

            if (transferenciaLog.foiProcessadaComSucesso()) {
            	log.info("[TRANSFERENCIA] Já processada anteriormente - KEY={}", idempotencyKey);
                return;
            }

            if (transferenciaLog.getStatus() == TransferenciaStatus.PROCESSANDO) {
                log.warn("[TRANSFERENCIA] ⚠️ Já está sendo processada por outra thread - KEY={}", idempotencyKey);
                throw new IllegalStateException("Transferência já está sendo processada");
            }

            log.info("[TRANSFERENCIA] 🔄 Retry de transferência que falhou - KEY={}", idempotencyKey);
        }

        TransferenciaLog transferenciaLog = criarLogTransferencia(request);

        try {
            ejbClient.transfer(
                    request.getFromId(),
                    request.getToId(),
                    request.getAmount()
            );

            atualizarLogSucesso(transferenciaLog.getId());

            log.info("[TRANSFERENCIA] Concluída com sucesso - KEY={}", idempotencyKey);

        } catch (Exception e) {
            atualizarLogErro(transferenciaLog.getId(), e.getMessage());

            log.error("[TRANSFERENCIA] Erro - KEY={}: {}", idempotencyKey, e.getMessage());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected TransferenciaLog criarLogTransferencia(TransferenciaRequest request) {
        TransferenciaLog transferenciaLog = new TransferenciaLog(
                request.getIdempotencyKey(),
                request.getFromId(),
                request.getToId(),
                request.getAmount()
        );
        transferenciaLog = transferenciaEjbService.save(transferenciaLog);
        log.debug("[TRANSFERENCIA] Log criado - ID={}", transferenciaLog.getId());
        return transferenciaLog;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void atualizarLogSucesso(Long logId) {
        TransferenciaLog transferenciaLog = transferenciaEjbService.findById(logId)
                .orElseThrow(() -> new IllegalStateException("Log não encontrado: " + logId));
        transferenciaLog.marcarComoProcessado();
        transferenciaEjbService.save(transferenciaLog);
        log.debug("[TRANSFERENCIA] Log atualizado para PROCESSADO - ID={}", logId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void atualizarLogErro(Long logId, String mensagemErro) {
        TransferenciaLog transferenciaLog = transferenciaEjbService.findById(logId)
                .orElseThrow(() -> new IllegalStateException("Log não encontrado: " + logId));
        transferenciaLog.marcarComoErro(mensagemErro);
        transferenciaEjbService.save(transferenciaLog);
        log.debug("[TRANSFERENCIA] Log atualizado para ERRO - ID={}", logId);
    }
}