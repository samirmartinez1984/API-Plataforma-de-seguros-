package com.insurance_platform_springboot.dtos.request;

import com.insurance_platform_springboot.model.enums.PartnerStatus;
import com.insurance_platform_springboot.model.enums.PartnerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de un partner/proveedor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PartnerRequestDTO", description = "Datos necesarios para crear un partner")
public class PartnerRequestDTO {

    @Schema(description = "Nombre del partner", example = "Proveedor S.A.")
    @NotBlank(message = "El nombre del partner es obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String partnerName;

    @Schema(description = "Tipo de partner (ej. WORKSHOP, CLINIC)", example = "WORKSHOP")
    @NotNull(message = "El typo no puede ser nulo")
    private PartnerType type;

    @Schema(description = "Dirección", example = "Calle Falsa 123", nullable = true)
    private String address;

    @Schema(description = "Teléfono de contacto", example = "+34 600 000 000", nullable = true)
    @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
    private String phone;

    @Schema(description = "Correo electrónico de contacto", example = "contacto@proveedor.com", nullable = true)
    @Email(message = "Debe ser un correo válido")
    private String email;

    @Schema(description = "Estado inicial del partner (ACTIVE/INACTIVE).", example = "ACTIVE", nullable = true)
    private PartnerStatus status;
}