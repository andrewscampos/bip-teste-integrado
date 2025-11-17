package com.bip.ejb.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "beneficio") 
@Data
@AllArgsConstructor
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "Nome do benefício é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Size(max = 255, message = "Descrição não pode exceder 255 caracteres")
    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @NotNull(message = "Valor do benefício é obrigatório")
    @DecimalMin(value = "0.00", inclusive = true, message = "Valor não pode ser negativo")
    @Column(name = "SALDO", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "ATIVO")
    private Boolean ativo = true;

    @Version
    @Column(name = "VERSION")
    private Long version;

    public Beneficio() {
        this.ativo = true;
    }

    public Beneficio(String nome, String descricao, BigDecimal valor) {
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = true;
    }

    // Métodos de negócio
    public void validarAtivo() {
        if (!Boolean.TRUE.equals(this.ativo)) {
            throw new IllegalStateException(
                    String.format("Benefício ID %d está inativo e não pode ser utilizado", this.id)
            );
        }
    }

    public void validarSaldoSuficiente(BigDecimal valor) {
        if (this.valor.compareTo(valor) < 0) {
//            throw new SaldoInsuficienteException(
//                    String.format("Saldo insuficiente. Disponível: %s, Solicitado: %s",
//                            this.valor, valor)
//            );
        }
    }

    public void debitar(BigDecimal valor) {
        validarAtivo();
        validarSaldoSuficiente(valor);
        this.valor = this.valor.subtract(valor);
    }

    public void creditar(BigDecimal valor) {
        validarAtivo();
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor a creditar deve ser positivo");
        }
        this.valor = this.valor.add(valor);
    }

      
}
