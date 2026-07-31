package com.insurance_platform_springboot.dtos.authDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para el registro de nuevos usuarios en la plataforma.
 */
@Schema(description = "Solicitud de registro de nuevo usuario")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Schema(description = "Nombre de usuario único", example = "juanp88")
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @Schema(description = "Email único del usuario", example = "juan@example.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Contraseña para la nueva cuenta (mínimo 8 caracteres)", example = "secreto1234")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
