package com.insurance_platform_springboot.dtos.update;

import com.insurance_platform_springboot.model.enums.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO para actualizaciones parciales de un reclamo (se permiten campos opcionales).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar un reclamo (partial update)")
public class ClaimUpdateDTO {

	@Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
	private String description;

	private ClaimStatus status;

	private Long supervisorId;

	private Long partnerId;

	@PastOrPresent(message = "La fecha de reporte no puede ser futura")
	private LocalDateTime reportedAt;

}
