package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.response.UserResponseDTO;
import com.insurance_platform_springboot.dtos.update.UserUpdateDTO;
import com.insurance_platform_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión administrativa de usuarios.
 * Solo accesible por personal con rol de Administrador.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión administrativa de usuarios")
@PreAuthorize("hasRole('ADMIN')") // Protege todo el controlador globalmente
public class UserController {

    private final UserService userService;

    /**
     * Obtiene la lista de todos los usuarios registrados.
     */
    @GetMapping
    @Operation(summary = "Listar todos los usuarios (Solo ADMIN)")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.listarUsuarios());
    }

    /**
     * Busca un usuario por su identificador único.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID (Solo ADMIN)")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(userService.buscarPorId(id));
    }

    /**
     * Elimina un usuario del sistema de forma permanente.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario (Solo ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza la información de un usuario existente.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información de usuario (Solo ADMIN)")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto){
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }
}