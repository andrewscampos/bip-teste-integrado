package com.bip.ejb.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.bip.ejb.model.Beneficio;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Stateless
public class BeneficioEjbService {

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

        from.validarAtivo();
        to.validarAtivo();

        BigDecimal saldoAtual = from.getValor();
        if (saldoAtual.compareTo(amount) < 0) {
            throw new IllegalStateException(
                String.format("Saldo insuficiente. Disponível: %s, Solicitado: %s", 
                              saldoAtual, amount)
            );
        }

        from.debitar(amount);
        to.creditar(amount);

        em.merge(from);
        em.merge(to);
    }

     
    public List<Beneficio> findByAtivoTrue() {
        return em.createQuery(
                "SELECT b FROM Beneficio b WHERE b.ativo = TRUE ORDER BY b.nome", 
                Beneficio.class)
            .getResultList();
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


    public Optional<Beneficio> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Beneficio beneficio = em.find(Beneficio.class, id);
        return Optional.ofNullable(beneficio);
    }


	public Beneficio save(Beneficio beneficio) {
		em.persist(beneficio);
		return beneficio;
	}

}
