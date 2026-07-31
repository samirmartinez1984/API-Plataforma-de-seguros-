package com.insurance_platform_springboot.mapper;

import com.insurance_platform_springboot.dtos.request.PartnerRequestDTO;
import com.insurance_platform_springboot.dtos.response.PartnerResponseDTO;
import com.insurance_platform_springboot.dtos.update.PartnerUpdateDTO;
import com.insurance_platform_springboot.model.Partner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para convertir entre la entidad {@link Partner} y sus DTOs.
 * Implementación generada por MapStruct.
 */
@Mapper(componentModel = "spring")
public interface PartnerMapper {

    /**
     * Convierte una entidad Partner a PartnerResponseDTO.
     */
    PartnerResponseDTO toResponse(Partner partner);

    /**
     * Convierte PartnerRequestDTO a entidad Partner. Ignora id, createdAt y relaciones gestionadas por el
     * servicio (como claims).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "claims", ignore = true)
    Partner toEntity(PartnerRequestDTO dto);

    /**
     * Actualiza una entidad Partner existente con los campos no nulos del DTO de actualización.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "claims", ignore = true)
    void updateEntity(PartnerUpdateDTO dto, @MappingTarget Partner partner);

}

