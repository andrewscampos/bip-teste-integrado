package com.bip.backend.beneficio.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.StaleStateException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bip.backend.beneficio.backend.mapper.BeneficioMapper;
import com.bip.backend.beneficio.dto.request.BeneficioCreateRequest;
import com.bip.backend.beneficio.dto.request.TransferenciaRequest;
import com.bip.backend.beneficio.dto.response.BeneficioResponse;
import com.bip.backend.beneficio.exception.BeneficioNotFoundException;
import com.bip.backend.beneficio.service.BeneficioService;
import com.bip.ejb.model.Beneficio;
import com.bip.ejb.service.BeneficioEjbService;
import com.bip.ejb.service.TransferenciaEjbService;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BeneficioServiceImpl implements BeneficioService {


    private final BeneficioEjbService beneficioEjbService;
    private final BeneficioMapper mapper;
    private final TransferenciaEjbService transferenciaService;

    public BeneficioServiceImpl(BeneficioEjbService beneficioEjbService,
                                BeneficioMapper mapper,
                                TransferenciaEjbService transferenciaService) {
        this.beneficioEjbService = beneficioEjbService;
        this.mapper = mapper;
        this.transferenciaService = transferenciaService;
    }

    @Override
    public List<BeneficioResponse> findAll() {
        log.debug("Buscando todos os benefícios ativos");
        return beneficioEjbService.findByAtivoTrue()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BeneficioResponse findById(Long id) {
        log.debug("Buscando benefício por ID: {}", id);
        Beneficio beneficio = beneficioEjbService.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));
        return mapper.toResponse(beneficio);
    }
 
    @Override
    @Transactional
    public BeneficioResponse create(BeneficioCreateRequest request) {
        log.info("Criando novo benefício: {}", request.getNome());
        Beneficio beneficio = mapper.toEntity(request);
        Beneficio saved = beneficioEjbService.save(beneficio);
        log.info("Benefício criado com sucesso: ID={}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = {OptimisticLockException.class, StaleStateException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 50, multiplier = 2, maxDelay = 500)
    )
    public BeneficioResponse update(Long id, BeneficioCreateRequest request) {
        log.info("Atualizando benefício ID: {}", id);

        try {
            Beneficio beneficio = beneficioEjbService.findById(id)
                    .orElseThrow(() -> new BeneficioNotFoundException(id));

            if (!Boolean.TRUE.equals(beneficio.getAtivo()) &&
                    !Boolean.TRUE.equals(request.getAtivo())) {
                throw new IllegalStateException("Não é possível atualizar benefício inativo");
            }

            beneficio.setNome(request.getNome());
            beneficio.setDescricao(request.getDescricao());
            beneficio.setValor(request.getValor());
            if (request.getAtivo() != null) {
                beneficio.setAtivo(request.getAtivo());
            }

            Beneficio updated = beneficioEjbService.save(beneficio);
            log.info("Benefício atualizado com sucesso: ID={}, VERSION={}",
                    id, updated.getVersion());

            return mapper.toResponse(updated);

        } catch (OptimisticLockException e) {
            log.warn("Conflito de concorrência no update do benefício ID: {}", id);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Desativando benefício ID: {}", id);
        Beneficio beneficio = beneficioEjbService.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));
        beneficio.setAtivo(false);
        beneficioEjbService.save(beneficio);
        log.info("Benefício desativado com sucesso: ID={}", id);
    }

    @Override
    public void transfer(TransferenciaRequest request) {
        transferenciaService.executarTransferencia(request);
    }
}