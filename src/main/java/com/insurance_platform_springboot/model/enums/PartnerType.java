package com.insurance_platform_springboot.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tipos de partner/proveedor.
 */
@Schema(description = "Tipos de partner/proveedor")
public enum PartnerType {
    /** Taller. */
    WORKSHOP,

    /** Clínica. */
    CLINIC,

    /** Laboratorio. */
    LABORATORY,

    /** Veterinaria. */
    VETERINARY
}
