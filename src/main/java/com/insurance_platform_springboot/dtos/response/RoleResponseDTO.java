package com.insurance_platform_springboot.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para roles. Se utiliza para exponer datos de roles a clientes.
 */
@Schema(name = "RoleResponseDTO", description = "Representación pública de un rol")
public record RoleResponseDTO(
        @Schema(description = "Identificador del rol", example = "1") Long id,
        @Schema(description = "Nombre del rol", example = "ADMIN") String name,
        @Schema(description = "Descripción del rol", example = "Administrador del sistema", nullable = true) String description
) {
}
