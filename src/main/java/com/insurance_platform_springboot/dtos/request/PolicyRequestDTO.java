package com.insurance_platform_springboot.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la creación de una póliza.
 * Se requiere el ID del cliente, id del producto y las fechas de vigencia.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PolicyRequestDTO", description = "Datos necesarios para crear una póliza")
public class PolicyRequestDTO {

	@Schema(description = "ID del cliente que contrata la póliza", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "El customerId es obligatorio")
	private Long customerId;

	@Schema(description = "ID del producto de catálogo asociado a la póliza", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "El productId es obligatorio")
	private Long productId;

	@Schema(description = "Fecha de inicio de la póliza (YYYY-MM-DD)", example = "2026-06-10", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La fecha de inicio es obligatoria")
	@FutureOrPresent(message = "La fecha de inicio debe ser hoy o futura")
	private LocalDate startDate;

	@Schema(description = "Fecha de fin de la póliza (YYYY-MM-DD)", example = "2027-06-10", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "La fecha de fin es obligatoria")
	@Future(message = "La fecha de fin debe ser futura")
	private LocalDate endDate;

	@Schema(description = "Precio final de la póliza (opcional en creación, se calcula si no se provee)", example = "199.99", nullable = true)
	private BigDecimal finalPrice;

	@Schema(description = "Porcentaje de descuento aplicado (ej. 10 para 10%)", example = "10", nullable = true)
	private Integer discountPercentage;

	@Schema(description = "Cargos extra o adicionales aplicados a la póliza", example = "25.00", nullable = true)
	private BigDecimal extraCharges;
}
