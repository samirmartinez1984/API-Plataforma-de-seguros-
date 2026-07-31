package com.insurance_platform_springboot.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para exponer información pública de un partner.
 */
@Schema(name = "PartnerResponseDTO", description = "Representación pública de un partner")
public record PartnerResponseDTO(
        @Schema(description = "Identificador del partner", example = "1") Long id,
        @Schema(description = "Nombre del partner", example = "Proveedor S.A.") String partnerName,
        @Schema(description = "Tipo de partner", example = "SERVICE") String type,
        @Schema(description = "Dirección", example = "Calle Falsa 123", nullable = true) String address,
        @Schema(description = "Teléfono", example = "+34 600 000 000", nullable = true) String phone,
        @Schema(description = "Correo electrónico", example = "contacto@proveedor.com", nullable = true) String email,
        @Schema(description = "Estado del partner", example = "ACTIVE") String status,
        @Schema(description = "Fecha de creación", example = "2026-06-10T12:00:00") LocalDateTime createdAt
) {
}

