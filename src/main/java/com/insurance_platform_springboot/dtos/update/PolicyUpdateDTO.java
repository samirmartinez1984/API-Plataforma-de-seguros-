package com.insurance_platform_springboot.dtos.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para actualizar una póliza. Los campos son opcionales; si son null no se modificarán.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PolicyUpdateDTO", description = "Campos opcionales para actualizar una póliza")
public class PolicyUpdateDTO {

	@Schema(description = "Nueva fecha de fin de la póliza (YYYY-MM-DD)", example = "2027-06-10", nullable = true)
	@Future(message = "La fecha de fin debe ser futura")
	private LocalDate endDate;

	@Schema(description = "Nuevo estado de la póliza", example = "CANCELLED", nullable = true)
	private String status;

	@Schema(description = "Nuevo precio final", example = "179.99", nullable = true)
	private BigDecimal finalPrice;

}
