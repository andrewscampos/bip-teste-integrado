package com.bip.backend.beneficio.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bip.backend.beneficio.dto.request.BeneficioCreateRequest;
import com.bip.backend.beneficio.dto.request.TransferenciaRequest;
import com.bip.backend.beneficio.dto.response.BeneficioResponse;
import com.bip.backend.beneficio.service.BeneficioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/beneficios")
@CrossOrigin(origins = "*") 
public class BeneficioController {

	private static final Logger logger = LoggerFactory.getLogger(BeneficioController.class);

	private final BeneficioService service;

	public BeneficioController(BeneficioService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<BeneficioResponse>> listAll() {
		logger.info("GET /api/v1/beneficios - Listando todos os benefícios");
		List<BeneficioResponse> beneficios = service.findAll();
		logger.info("Retornando {} benefícios", beneficios.size());
		return ResponseEntity.ok(beneficios);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BeneficioResponse> findById(@PathVariable Long id) {
		logger.info("GET /api/v1/beneficios/{} - Buscando benefício", id);
		BeneficioResponse beneficio = service.findById(id);
		return ResponseEntity.ok(beneficio);
	}

	@PostMapping
	public ResponseEntity<BeneficioResponse> create(@Valid @RequestBody BeneficioCreateRequest request) {

		logger.info("POST /api/v1/beneficios - Criando benefício: {}", request);
		BeneficioResponse created = service.create(request);
		logger.info("Benefício criado com ID: {}", created.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BeneficioResponse> update(@PathVariable Long id,
			@Valid @RequestBody BeneficioCreateRequest request) {
		logger.info("PUT /api/v1/beneficios/{} - Atualizando benefício", id);
		BeneficioResponse updated = service.update(id, request);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {

		logger.info("DELETE /api/v1/beneficios/{} - Desativando benefício", id);
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/transfer")
	public ResponseEntity<Void> transfer(@Valid @RequestBody TransferenciaRequest request) {

		logger.info("POST /api/v1/beneficios/transfer - Transferência: FROM={}, TO={}, AMOUNT={}", request.getFromId(),
				request.getToId(), request.getAmount());

		service.transfer(request);

		logger.info("Transferência concluída com sucesso");
		return ResponseEntity.ok().build();
	}
}
