package com.bip.backend.beneficio.dto.request;


import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferenciaRequest {

	 @NotNull(message = "ID do benefício origem é obrigatório")
	    private Long fromId;

	    @NotNull(message = "ID do benefício destino é obrigatório")
	    private Long toId;

	    @NotNull(message = "Valor da transferência é obrigatório")
	    @DecimalMin(value = "0.01", inclusive = true, message = "Valor deve ser maior que zero")
	    @Digits(integer = 13, fraction = 2, message = "Valor deve ter no máximo 13 dígitos inteiros e 2 decimais")
	    private BigDecimal amount;

	    private String idempotencyKey;

	    public TransferenciaRequest() {
	        this.idempotencyKey = UUID.randomUUID().toString();
	    }


    
}
