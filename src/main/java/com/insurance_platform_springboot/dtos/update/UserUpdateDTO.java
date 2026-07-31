package com.insurance_platform_springboot.dtos.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para actualizar un usuario. Todos los campos son opcionales;
 * si un campo es null, no se actualizará en la entidad.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UserUpdateDTO", description = "Campos opcionales para actualizar un usuario")
public class UserUpdateDTO {

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez", nullable = true)
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String name;

    @Schema(description = "Correo electrónico", example = "juan@example.com", nullable = true)
    @Email(message = "Debe ser correo valido")
    private String email;

    @Schema(description = "Estado de la cuenta (true = habilitada)", example = "true", nullable = true)
    private Boolean enabled;

    @Schema(description = "ID del rol asignado al usuario", example = "1", nullable = true)
    private Long roleId;

}
