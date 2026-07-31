package com.insurance_platform_springboot.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con la representación pública de un usuario.
 * Se utiliza para devolver información al cliente sin exponer datos sensibles.
 */
@Schema(name = "UserResponseDTO", description = "Representación pública de un usuario")
public record UserResponseDTO(

        @Schema(description = "Identificador del usuario", example = "1") Long id,
        @Schema(description = "Nombre completo", example = "Juan Pérez") String name,
        @Schema(description = "Nombre de usuario", example = "juanperez") String username,
        @Schema(description = "Correo electrónico", example = "juan@example.com") String email,
        @Schema(description = "Nombre del rol asignado al usuario", example = "USER") String roleName,
        @Schema(description = "Indica si la cuenta está habilitada", example = "true") Boolean enabled,
        @Schema(description = "Fecha y hora de registro", example = "2024-01-01T12:00:00") LocalDateTime registeredAt
        ) {}
