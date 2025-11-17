package com.bip.backend.beneficio.service;

import com.bip.backend.beneficio.dto.request.TransferenciaRequest;

public interface TransferenciaService {

    void executarTransferencia(TransferenciaRequest request);

}