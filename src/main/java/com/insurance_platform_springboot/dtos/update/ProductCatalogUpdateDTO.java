package com.insurance_platform_springboot.dtos.update;

import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para actualizaciones parciales de un producto del catálogo.
 * Todos los campos son opcionales; solo se actualizan los campos no-null.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar un producto del catálogo (partial update)")
public class ProductCatalogUpdateDTO {

    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    @Schema(description = "Nuevo nombre del producto (opcional)", example = "seguro de Automóvil Premium")
    private String nameProduct;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    @Schema(description = "Nueva descripción del producto (opcional)", example = "Cobertura mejorada con asistencia premium")
    private String description;

    @Size(max = 500, message = "La cobertura no puede superar 500 caracteres")
    @Schema(description = "Nuevo tipo de cobertura (opcional)", example = "Daños totales incluidos")
    private String coverage;

    @Size(max = 1000, message = "Las exclusiones no pueden superar 1000 caracteres")
    @Schema(description = "Nuevas exclusiones (opcional)", example = "Conductores no autorizados")
    private String exclusions;

    @DecimalMin(value = "0.01", message = "El precio base debe ser mayor a 0")
    @Schema(description = "Nuevo precio base (opcional)", example = "119.99")
    private BigDecimal basePrice;

    @Schema(description = "Nuevo tipo de producto (opcional)", example = "LIFE")
    private ProductType type;

    @Schema(description = "Nuevo estado del producto (opcional)", example = "INACTIVE")
    private ProductStatus status;
}
