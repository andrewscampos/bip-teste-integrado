package com.bip.backend.beneficio.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bip.backend.beneficio.dto.request.BeneficioCreateRequest;
import com.bip.backend.beneficio.dto.request.TransferenciaRequest;
import com.bip.backend.beneficio.dto.response.BeneficioResponse;

@Service
public interface BeneficioService {

    List<BeneficioResponse> findAll();

    BeneficioResponse findById(Long id);

    BeneficioResponse create(BeneficioCreateRequest request);

    BeneficioResponse update(Long id, BeneficioCreateRequest request);

    void delete(Long id);

    void transfer(TransferenciaRequest request);
}