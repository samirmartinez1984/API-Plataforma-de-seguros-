package com.insurance_platform_springboot.dtos.response;

import com.insurance_platform_springboot.model.enums.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para un reclamo.
 */
@Schema(name = "ClaimResponseDTO", description = "Representación pública de un reclamo")
public record ClaimResponseDTO(
        @Schema(description = "Identificador del reclamo", example = "1") Long id,
        @Schema(description = "ID de la póliza asociada", example = "10") Long policyId,
        @Schema(description = "ID del cliente que realiza el reclamo", example = "2") Long customerId,
        @Schema(description = "ID del supervisor asignado", example = "3") Long supervisorId,
        @Schema(description = "ID del partner que presta el servicio", example = "5", nullable = true) Long partnerId,
        @Schema(description = "Descripción del incidente", example = "Choque frontal en la vía principal") String description,
        @Schema(description = "Estado actual del reclamo", example = "OPEN") ClaimStatus status,
        @Schema(description = "Fecha y hora en que se reportó", example = "2026-06-12T14:30:00") LocalDateTime reportedAt,
        @Schema(description = "Fecha y hora de creación en el sistema", example = "2026-06-12T14:35:00") LocalDateTime createdAt
) {
}
