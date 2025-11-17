package com.bip.backend.beneficio.backend.mapper;

import com.bip.backend.beneficio.dto.request.BeneficioCreateRequest;
import com.bip.backend.beneficio.dto.response.BeneficioResponse;
import com.bip.ejb.model.Beneficio;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-16T23:20:00-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.36.0.v20231114-0937, environment: Java 17.0.9 (Eclipse Adoptium)"
)
@Component
public class BeneficioMapperImpl implements BeneficioMapper {

    @Override
    public BeneficioResponse toResponse(Beneficio entity) {
        if ( entity == null ) {
            return null;
        }

        BeneficioResponse beneficioResponse = new BeneficioResponse();

        beneficioResponse.setAtivo( entity.getAtivo() );
        beneficioResponse.setDescricao( entity.getDescricao() );
        beneficioResponse.setId( entity.getId() );
        beneficioResponse.setNome( entity.getNome() );
        beneficioResponse.setValor( entity.getValor() );
        beneficioResponse.setVersion( entity.getVersion() );

        return beneficioResponse;
    }

    @Override
    public Beneficio toEntity(BeneficioCreateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Beneficio beneficio = new Beneficio();

        beneficio.setAtivo( dto.getAtivo() );
        beneficio.setDescricao( dto.getDescricao() );
        beneficio.setNome( dto.getNome() );
        beneficio.setValor( dto.getValor() );

        return beneficio;
    }

    @Override
    public void updateEntityFromDto(BeneficioResponse dto, Beneficio entity) {
        if ( dto == null ) {
            return;
        }

        entity.setAtivo( dto.getAtivo() );
        entity.setDescricao( dto.getDescricao() );
        entity.setId( dto.getId() );
        entity.setNome( dto.getNome() );
        entity.setValor( dto.getValor() );
        entity.setVersion( dto.getVersion() );
    }
}
