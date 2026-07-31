package com.insurance_platform_springboot.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tipos de producto disponibles en el catálogo.
 */
@Schema(description = "Tipos de producto disponibles en el catálogo")
public enum ProductType {
    /** Automóvil */
    AUTO,

    /** Vida */
    LIFE,

    /** Salud */
    HEALTH,

    /** Mascotas */
    PETS,

    /** Hogar */
    HOME
}
