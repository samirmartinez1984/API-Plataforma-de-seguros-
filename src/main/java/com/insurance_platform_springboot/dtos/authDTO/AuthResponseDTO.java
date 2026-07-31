package com.insurance_platform_springboot.dtos.authDTO;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO que contiene la respuesta tras una autenticación exitosa.
 * Incluye el token de acceso y datos básicos del usuario.
 */
@Schema(description = "Respuesta de autenticación exitosa")
public record AuthResponseDTO(
        @Schema(description = "Token JWT de acceso para rutas protegidas", example = "eyJhbGciOiJIUzUxMiJ9...")
        String token,

        @Schema(description = "Email del usuario autenticado", example = "admin@seguros.com")
        String email,

        @Schema(description = "Nombre del rol asignado al usuario", example = "ROLE_USER")
        String role
) {
}
