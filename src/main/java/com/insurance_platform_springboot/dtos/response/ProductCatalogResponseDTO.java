package com.insurance_platform_springboot.dtos.response;

import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para un producto del catálogo.
 */
@Schema(name = "ProductCatalogResponseDTO", description = "Representación pública de un producto del catálogo")
public record ProductCatalogResponseDTO(
        @Schema(description = "Identificador único del producto", example = "1") Long id,
        @Schema(description = "Nombre del producto", example = "Seguro de Automóvil Completo") String nameProduct,
        @Schema(description = "Descripción detallada", example = "Cobertura integral para automóviles") String description,
        @Schema(description = "Tipo de cobertura", example = "Daños a terceros, robo, colisión") String coverage,
        @Schema(description = "Exclusiones aplicables", example = "Uso comercial") String exclusions,
        @Schema(description = "Precio base del producto", example = "99.99") BigDecimal basePrice,
        @Schema(description = "Tipo de producto", example = "AUTO") ProductType type,
        @Schema(description = "Estado del producto", example = "ACTIVE") ProductStatus status,
        @Schema(description = "Fecha de creación", example = "2026-06-11T10:15:30") LocalDateTime createdAt
) {
}
