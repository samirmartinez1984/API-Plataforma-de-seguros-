package com.insurance_platform_springboot.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estado de un producto del catálogo.
 */
@Schema(description = "Estado de un producto del catálogo")
public enum ProductStatus {
    /** Producto activo. */
    ACTIVE,

    /** Producto inactivo. */
    INACTIVE
}
