package com.insurance_platform_springboot.dtos.response;

import com.insurance_platform_springboot.model.enums.PolicyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta que resume la información de una póliza.
 */
@Schema(name = "PolicyResponseDTO", description = "Representación pública de una póliza")
public record PolicyResponseDTO(
        @Schema(description = "Identificador de la póliza", example = "1") Long id,
        @Schema(description = "ID del cliente", example = "2") Long customerId,
        @Schema(description = "ID del producto", example = "10") Long productId,
        @Schema(description = "Fecha de inicio de vigencia", example = "2026-06-10") LocalDate startDate,
        @Schema(description = "Fecha de fin de vigencia", example = "2027-06-10") LocalDate endDate,
        @Schema(description = "Estado actual de la póliza", example = "ACTIVE") PolicyStatus status,
        @Schema(description = "Precio final calculado", example = "199.99") BigDecimal finalPrice,
        @Schema(description = "Fecha de creación en el sistema", example = "2026-06-10T12:00:00") LocalDateTime createdAt,
        BigDecimal basePrice,
        Integer discountPercentage,
        BigDecimal extraCharges
) {
}
