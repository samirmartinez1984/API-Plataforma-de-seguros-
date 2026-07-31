package com.insurance_platform_springboot.mapper;

import com.insurance_platform_springboot.dtos.request.ClaimRequestDTO;
import com.insurance_platform_springboot.dtos.response.ClaimResponseDTO;
import com.insurance_platform_springboot.dtos.update.ClaimUpdateDTO;
import com.insurance_platform_springboot.model.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para convertir entre la entidad {@link Claim} y sus DTOs.
 * Implementación generada por MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ClaimMapper {

    /**
     * Convierte Claim a ClaimResponseDTO extrayendo los ids de las relaciones.
     */
    @Mapping(source = "policy.id", target = "policyId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "supervisor.id", target = "supervisorId")
    @Mapping(source = "partner.id", target = "partnerId")
    ClaimResponseDTO toResponse(Claim claim);

    /**
     * Convierte ClaimRequestDTO a entidad Claim. Ignora id y relaciones; el service debe resolver las
     * entidades relacionadas (policy/customer/supervisor/partner).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "policy", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "supervisor", ignore = true)
    @Mapping(target = "partner", ignore = true)
    Claim toEntity(ClaimRequestDTO dto);

    /**
     * Aplica actualizaciones parciales a una entidad Claim existente.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policy", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "supervisor", ignore = true)
    @Mapping(target = "partner", ignore = true)
    void updateEntity(ClaimUpdateDTO dto, @MappingTarget Claim claim);

}

