package com.insurance_platform_springboot.mapper;

import com.insurance_platform_springboot.dtos.authDTO.RegisterRequestDTO;
import com.insurance_platform_springboot.dtos.request.UserRequestDTO;
import com.insurance_platform_springboot.dtos.response.UserResponseDTO;
import com.insurance_platform_springboot.dtos.update.UserUpdateDTO;
import com.insurance_platform_springboot.model.auth.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para convertir entre la entidad {@link User} y sus DTOs.
 * La implementación es generada por MapStruct en tiempo de compilación.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Mapea entidad User a UserResponseDTO. Asume que el campo role puede ser nulo.
     */
    @Mapping(source = "role.name", target = "roleName")
    UserResponseDTO toResponse(User user);

    /**
     * Mapea UserRequestDTO a entidad User. Ignora campos que deben ser gestionados por el service
     * (id, fechas generadas, role y passwordHash).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserRequestDTO dto);

    /**
     * Actualiza una entidad User existente con los campos no nulos de UserUpdateDTO.
     * Ignora id/fechas y delega la actualización de relaciones al service.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateEntity(UserUpdateDTO dto, @MappingTarget User user);

    /**
     * Mapea RegisterRequestDTO a la entidad User.
     * Ignora los campos que el servicio debe gestionar manualmente
     * (id, passwordHash, role, enabled, fechas).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "policies", ignore = true)
    @Mapping(target = "claimsCustomer", ignore = true)
    @Mapping(target = "claimsSupervisor", ignore = true)
    User registerToEntity(RegisterRequestDTO dto);

}
