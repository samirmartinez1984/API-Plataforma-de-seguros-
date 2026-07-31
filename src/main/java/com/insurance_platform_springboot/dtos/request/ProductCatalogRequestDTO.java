package com.insurance_platform_springboot.dtos.request;

import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para la creación de un producto del catálogo.
 * Contiene los datos básicos del producto: nombre, descripción, cobertura, precio y tipo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para crear un producto del catálogo")
public class ProductCatalogRequestDTO {

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    @Schema(description = "Nombre del producto", example = "seguro de Automóvil Completo")
    private String nameProduct;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    @Schema(description = "Descripción detallada del producto", example = "Cobertura integral para automóviles con asistencia 24/7")
    private String description;

    @NotBlank(message = "El tipo de cobertura no puede estar vacío")
    @Size(max = 500, message = "La cobertura no puede superar 500 caracteres")
    @Schema(description = "Tipo de cobertura ofrecida", example = "Daños a terceros, robo, colisión")
    private String coverage;

    @Size(max = 1000, message = "Las exclusiones no pueden superar 1000 caracteres")
    @Schema(description = "Exclusiones aplicables (opcional)", example = "Uso comercial, conductores no autorizados")
    private String exclusions;

    @NotNull(message = "El precio base no puede ser nulo")
    @DecimalMin(value = "0.01", message = "El precio base debe ser mayor a 0")
    @Schema(description = "Precio base del producto", example = "99.99")
    private BigDecimal basePrice;

    @NotNull(message = "El tipo de producto no puede ser nulo")
    @Schema(description = "Tipo de producto (AUTO, LIFE, HEALTH, PETS, HOME)", example = "AUTO")
    private ProductType type;

    @NotNull(message = "El estado del producto no puede ser nulo")
    @Schema(description = "Estado del producto (ACTIVE o INACTIVE)", example = "ACTIVE")
    private ProductStatus status;

}
