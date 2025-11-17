package com.bip.ejb.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "TRANSFERENCIA_LOG", indexes = {
		@Index(name = "idx_idempotency_key", columnList = "idempotency_key", unique = true),
		@Index(name = "idx_created_at", columnList = "created_at") })
@Data
@EqualsAndHashCode
public class TransferenciaLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "idempotency_key", unique = true, nullable = false, length = 100)
	private String idempotencyKey;

	@Column(name = "from_id", nullable = false)
	private Long fromId;

	@Column(name = "to_id", nullable = false)
	private Long toId;

	@Column(name = "amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private TransferenciaStatus status;

	@Column(name = "error_message", length = 500)
	private String errorMessage;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public TransferenciaLog() {
		this.createdAt = LocalDateTime.now();
	}

	public TransferenciaLog(String idempotencyKey, Long fromId, Long toId, BigDecimal amount) {
		this.idempotencyKey = idempotencyKey;
		this.fromId = fromId;
		this.toId = toId;
		this.amount = amount;
		this.status = TransferenciaStatus.PROCESSANDO;
		this.createdAt = LocalDateTime.now();
	}

	public void marcarComoProcessado() {
		this.status = TransferenciaStatus.PROCESSADO;
		this.updatedAt = LocalDateTime.now();
	}

	public void marcarComoErro(String mensagemErro) {
		this.status = TransferenciaStatus.ERRO;
		this.errorMessage = mensagemErro;
		this.updatedAt = LocalDateTime.now();
	}

	public boolean foiProcessadaComSucesso() {
		return TransferenciaStatus.PROCESSADO.equals(this.status);
	}
 

 
}