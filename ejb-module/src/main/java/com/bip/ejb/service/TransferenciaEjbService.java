package com.bip.ejb.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.bip.ejb.model.Beneficio;
import com.bip.ejb.model.TransferenciaLog;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Stateless
public class TransferenciaEjbService {

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void transfer(Long fromId, Long toId, BigDecimal amount) {

		validateTransferInput(fromId, toId, amount);

		Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
		Beneficio to = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);

		if (from == null) {
			throw new IllegalArgumentException("Conta origem não encontrada: " + fromId);
		}
		if (to == null) {
			throw new IllegalArgumentException("Conta destino não encontrada: " + toId);
		}

		BigDecimal currentBalance = from.getValor();
		if (currentBalance.compareTo(amount) < 0) {
			throw new IllegalStateException(
					String.format("Saldo insuficiente. Disponível: %s, Solicitado: %s", currentBalance, amount));
		}

		from.setValor(currentBalance.subtract(amount));
		to.setValor(to.getValor().add(amount));
	}

	private void validateTransferInput(Long fromId, Long toId, BigDecimal amount) {
		if (fromId == null || toId == null) {
			throw new IllegalArgumentException("IDs não podem ser nulos");
		}

		if (fromId.equals(toId)) {
			throw new IllegalArgumentException("Conta origem e destino não podem ser iguais");
		}

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Valor deve ser maior que zero");
		}
	}

	public Optional<TransferenciaLog> findByIdempotencyKey(String idempotencyKey) {
		if (idempotencyKey == null || idempotencyKey.isBlank()) {
			return Optional.empty();
		}

		try {
			TransferenciaLog log = em
					.createQuery("SELECT t FROM TransferenciaLog t WHERE t.idempotencyKey = :key",
							TransferenciaLog.class)
					.setParameter("key", idempotencyKey).setMaxResults(1).getSingleResult();

			return Optional.ofNullable(log);

		} catch (jakarta.persistence.NoResultException e) {
			return Optional.empty();
		}
	}

	public Optional<TransferenciaLog> findById(Long logId) {
		if (logId == null) {
			return Optional.empty();
		}

		TransferenciaLog log = em.find(TransferenciaLog.class, logId);
		return Optional.ofNullable(log);
	}

	@Transactional
	public TransferenciaLog save(TransferenciaLog transferenciaLog) {
		if (transferenciaLog == null) {
			throw new IllegalArgumentException("TransferênciaLog não pode ser nulo");
		}

		if (transferenciaLog.getId() == null) {
			em.persist(transferenciaLog);
			return transferenciaLog;
		} else {
			return em.merge(transferenciaLog);
		}
	}

	public void executarTransferencia(Object request) {
		// TODO Auto-generated method stub
		
	}
}