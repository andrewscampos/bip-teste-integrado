package com.bip.backend.beneficio.exception;

public class BeneficioNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 8758203143081175330L;
	
	public BeneficioNotFoundException(String msg) {
		super(msg);
	}
	
	public BeneficioNotFoundException(Object msg) {
		super(String.valueOf(msg));
	}

}
