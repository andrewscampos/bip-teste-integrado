package com.bip.backend.beneficio.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.bip.backend.beneficio.dto.request.BeneficioCreateRequest;
import com.bip.backend.beneficio.dto.response.BeneficioResponse;
import com.bip.ejb.model.Beneficio;

@Mapper(componentModel = "spring")
public interface BeneficioMapper {

    BeneficioResponse toResponse(Beneficio entity);

    Beneficio toEntity(BeneficioCreateRequest dto);

    void updateEntityFromDto(BeneficioResponse dto, @MappingTarget Beneficio entity);
}
