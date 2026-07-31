package com.insurance_platform_springboot.mapper;

import com.insurance_platform_springboot.dtos.request.RoleRequestDTO;
import com.insurance_platform_springboot.dtos.response.RoleResponseDTO;
import com.insurance_platform_springboot.dtos.update.RoleUpdateDTO;
import com.insurance_platform_springboot.model.auth.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para convertir entre la entidad {@link Role} y sus DTOs.
 * Implementación generada por MapStruct.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Convierte una entidad Role a RoleResponseDTO.
     */
    RoleResponseDTO toResponse(Role role);

    /**
     * Convierte RoleRequestDTO a entidad Role. Ignora id y campos gestionados automáticamente.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Role toEntity(RoleRequestDTO dto);

    /**
     * Actualiza una entidad Role existente con los campos no nulos del DTO de actualización.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(RoleUpdateDTO dto, @MappingTarget Role role);

}

