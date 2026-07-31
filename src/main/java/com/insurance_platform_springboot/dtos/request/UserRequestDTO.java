package com.insurance_platform_springboot.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO usado para crear un nuevo usuario (registro).
 * Contiene los datos mínimos necesarios para crear la entidad User.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UserRequestDTO", description = "Datos necesarios para registrar un usuario")
public class UserRequestDTO {

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String name;

    @Schema(description = "Nombre de usuario, único en el sistema", example = "juanperez")
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, message = "El nombre de usuario debe tener al menos 3 caracteres")
    private String username;

    @Schema(description = "Correo electrónico del usuario", example = "juan@example.com")
    @Email(message = "Debe ser un correo válido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    @Schema(description = "Contraseña (no se guarda en texto claro)", example = "P@s5w0rd!", minLength = 8)
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
