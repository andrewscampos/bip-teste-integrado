package com.bip.backend.beneficio.dto.response;


import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class BeneficioResponse {

    private Long id;

    private String nome;

    private String descricao;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal valor;

    private Boolean ativo;

    private Long version;

    
}
