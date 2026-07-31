package com.insurance_platform_springboot.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estados posibles de un reclamo (claim).
 */
@Schema(description = "Estados posibles de un reclamo")
public enum ClaimStatus {
    /** Reclamo registrado por el cliente. */
    REGISTERED,

    /** En revisión por el supervisor o área técnica. */
    IN_REVIEW,

    /** Reclamo aprobado y en proceso de pago/gestión. */
    APPROVED,

    /** Reclamo rechazado por no cumplir condiciones. */
    REJECTED
}
