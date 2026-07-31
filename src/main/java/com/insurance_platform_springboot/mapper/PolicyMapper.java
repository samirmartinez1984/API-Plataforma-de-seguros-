package com.insurance_platform_springboot.mapper;

import com.insurance_platform_springboot.dtos.request.PolicyRequestDTO;
import com.insurance_platform_springboot.dtos.response.PolicyResponseDTO;
import com.insurance_platform_springboot.dtos.update.PolicyUpdateDTO;
import com.insurance_platform_springboot.model.Policy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para convertir entre la entidad {@link Policy} y sus DTOs.
 * Implementación generada por MapStruct.
 */
@Mapper(componentModel = "spring")
public interface PolicyMapper {

    /**
     * Mapea entidad Policy a PolicyResponseDTO.
     */
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "customer.id", target = "customerId")
    PolicyResponseDTO toResponse(Policy policy);

    /**
     * Mapea PolicyRequestDTO a entidad Policy. Ignora id y relaciones que el service debe resolver.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "status", ignore = true)
    Policy toEntity(PolicyRequestDTO dto);

    /**
     * Aplica cambios parciales de PolicyUpdateDTO sobre una entidad existente.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntity(PolicyUpdateDTO dto, @MappingTarget Policy policy);

}

