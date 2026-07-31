package com.insurance_platform_springboot.dtos.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para actualizar un rol. Los campos son opcionales; si son null no se actualizarán.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RoleUpdateDTO", description = "Campos opcionales para actualizar un rol")
public class RoleUpdateDTO {

    @Schema(description = "Nombre del rol", example = "ADMIN", nullable = true)
    @Size(min = 3, message = "El nombre del rol debe tener al menos 3 caracteres")
    private String name;

    @Schema(description = "Descripción del rol", example = "Administrador del sistema", nullable = true)
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;
}
