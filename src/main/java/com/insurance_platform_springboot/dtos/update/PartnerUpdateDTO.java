package com.insurance_platform_springboot.dtos.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para actualizar un partner. Los campos son opcionales; si son null no se actualizarán.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PartnerUpdateDTO", description = "Campos opcionales para actualizar un partner")
public class PartnerUpdateDTO {

    @Schema(description = "Nombre del partner", example = "Proveedor S.A.", nullable = true)
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String partnerName;

    @Schema(description = "Tipo de partner (ej. SERVICE)", example = "SERVICE", nullable = true)
    private String type;

    @Schema(description = "Dirección", example = "Calle Falsa 123", nullable = true)
    private String address;

    @Schema(description = "Teléfono de contacto", example = "+34 600 000 000", nullable = true)
    @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
    private String phone;

    @Schema(description = "Correo electrónico de contacto", example = "contacto@proveedor.com", nullable = true)
    @Email(message = "Debe ser un correo válido")
    private String email;

    @Schema(description = "Estado del partner (ACTIVE/INACTIVE)", example = "ACTIVE", nullable = true)
    private String status;

}

