package com.insurance_platform_springboot.dtos.authDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la solicitud de inicio de sesión.
 */
@Schema(description = "Solicitud de inicio de sesión")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

    @Schema(description = "Correo electrónico del usuario", example = "usuario@seguros.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El email es obligatorio")
    @Email
    private String email;

    @Schema(description = "Contraseña en texto plano", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
