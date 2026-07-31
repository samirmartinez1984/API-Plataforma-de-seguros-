package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.request.RoleRequestDTO;
import com.insurance_platform_springboot.dtos.response.RoleResponseDTO;
import com.insurance_platform_springboot.dtos.update.RoleUpdateDTO;
import com.insurance_platform_springboot.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de roles en el sistema.
 * Permite realizar operaciones CRUD sobre los perfiles de acceso.
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints para la gestión de roles de usuario")
public class RoleController {
    
    private final RoleService roleService;

    /**
     * Crea un nuevo rol en el sistema.
     * @param roleRequestDTO Datos del rol a crear.
     * @return RoleResponseDTO con el rol creado.
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo rol")
    public ResponseEntity<RoleResponseDTO> createRol(@Valid @RequestBody RoleRequestDTO roleRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(roleRequestDTO));
    }

    /**
     * Obtiene la lista completa de roles.
     * @return Lista de RoleResponseDTO.
     */
    @GetMapping
    @Operation(summary = "Listar todos los roles")
    public ResponseEntity<List<RoleResponseDTO>> listarRoles(){
        return ResponseEntity.ok(roleService.getAll());
    }

    /**
     * Busca un rol por su identificador único.
     * @param id Identificador del rol.
     * @return RoleResponseDTO con la información del rol.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar rol por ID")
    public ResponseEntity<RoleResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(roleService.getById(id));
    }

    /**
     * Actualiza un rol existente.
     * @param id Identificador del rol a modificar.
     * @param roleUpdateDTO Datos actualizados.
     * @return RoleResponseDTO con el rol modificado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un rol existente")
    public ResponseEntity<RoleResponseDTO> actualizarRol(@PathVariable Long id, @Valid @RequestBody RoleUpdateDTO roleUpdateDTO){
        return ResponseEntity.ok(roleService.updateRole(id, roleUpdateDTO));
    }

    /**
     * Elimina un rol del sistema.
     * @param id Identificador del rol a eliminar.
     * @return No Content (204) si la operación fue exitosa.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un rol")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id){
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
