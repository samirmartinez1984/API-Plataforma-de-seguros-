
package com.insurance_platform_springboot.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de roles en el sistema.
 * Contiene el nombre (requerido) y una descripción opcional.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RoleRequestDTO", description = "Datos necesarios para crear un rol")
public class RoleRequestDTO {

    @Schema(description = "Nombre del rol (único)", example = "ADMIN")
    @NotBlank(message = "El nombre del rol es obligatorio")
    private String name;

    @Schema(description = "Descripción del rol", example = "Administrador del sistema", maxLength = 255, nullable = true)
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;
}
