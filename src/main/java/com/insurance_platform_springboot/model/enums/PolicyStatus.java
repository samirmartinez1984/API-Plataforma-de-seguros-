package com.insurance_platform_springboot.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estados posibles de una póliza.
 */
@Schema(description = "Estados posibles de una póliza")
public enum PolicyStatus {
    /** Póliza activa. */
    ACTIVE,

    /** Póliza expirada. */
    EXPIRED,

    DELETED,
    /** Póliza cancelada. */
    CANCELLED
}
