package com.insurance_platform_springboot.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estado de un partner/proveedor.
 */
@Schema(description = "Estado de un partner/proveedor")
public enum PartnerStatus {
    /** Partner activo. */
    ACTIVE,

    /** Partner inactivo. */
    INACTIVE
}
