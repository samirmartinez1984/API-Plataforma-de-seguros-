package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.authDTO.AuthResponseDTO;
import com.insurance_platform_springboot.dtos.authDTO.LoginRequestDTO;
import com.insurance_platform_springboot.dtos.authDTO.RegisterRequestDTO;
import com.insurance_platform_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador encargado de los procesos de autenticación y registro de usuarios.
 * Estos endpoints son públicos y no requieren token JWT previo.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro e inicio de sesión")
public class AuthController {

    private final UserService userService;

    /**
     * Registra un nuevo cliente en el sistema.
     * @param registerRequestDTO Datos del nuevo usuario.
     * @return AuthResponseDTO con el token generado tras el registro.
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo cliente (Auto-login)")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registerRequestDTO));
    }

    /**
     * Autentica a un usuario existente.
     * @param loginRequestDTO Credenciales de acceso.
     * @return AuthResponseDTO con el token JWT de acceso.
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(userService.login(loginRequestDTO));
    }
}
