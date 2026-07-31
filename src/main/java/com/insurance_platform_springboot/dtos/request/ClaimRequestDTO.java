package com.insurance_platform_springboot.dtos.request;

import com.insurance_platform_springboot.model.enums.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO para la creación de un reclamo (claim).
 * Nota: si no se envía {@code reportedAt}, el servicio puede asignar la fecha actual.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para crear un reclamo")
public class ClaimRequestDTO {

    @NotNull(message = "El id de la póliza no puede ser nulo")
    @Schema(description = "Id de la póliza asociada", example = "10")
    private Long policyId;

    @NotNull(message = "El id del customer no puede ser nulo")
    @Schema(description = "Id del cliente que reporta el reclamo", example = "2")
    private Long customerId;

    @NotNull(message = "El id del supervisor no puede ser nulo")
    @Schema(description = "Id del supervisor asignado", example = "5")
    private Long supervisorId;

    @NotNull(message = "El id del aliado no puede ser nulo")
    @Schema(description = "Id del partner/aliado relacionado", example = "3")
    private Long partnerId;

    @Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
    @Schema(description = "Descripción del reclamo", example = "Choque en la parte trasera del vehículo")
    private String description;

    @Schema(description = "Estado inicial del reclamo. Si no se envía, se usará REGISTERED", example = "REGISTERED")
    private ClaimStatus status;

    @PastOrPresent(message = "La fecha de reporte no puede ser futura")
    @Schema(description = "Fecha y hora en la que se reportó el reclamo (opcional). Si no se incluye, el servicio puede usar ahora.", example = "2026-06-11T10:15:30")
    private LocalDateTime reportedAt;

}
