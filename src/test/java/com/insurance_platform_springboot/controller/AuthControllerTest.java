package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.authDTO.LoginRequestDTO;
import com.insurance_platform_springboot.dtos.authDTO.RegisterRequestDTO;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración para el controlador de Autenticación (AuthController).
 * 
 * <p>Esta clase simula peticiones HTTP reales hacia los endpoints públicos de la API, 
 * validando que el ciclo completo (Controlador -> Servicio -> Seguridad -> BD) funcione. 
 * Utiliza H2 como base de datos en memoria para no afectar el entorno de producción.</p>
 */
public class AuthControllerTest extends BaseControllerTest {

    /** 
     * Inyección del repositorio real para poder "sembrar" los datos base que el sistema 
     * necesita (como los roles) antes de ejecutar las peticiones HTTP.
     */
    @Autowired
    private RoleRepository roleRepository;
    
    /**
     * Válida el endpoint de registro de un nuevo usuario en la plataforma.
     * 
     * <p>Contexto: Como usamos H2 (una BD en memoria que nace vacía), primero debemos 
     * crear el rol 'ROLE_USER', de lo contrario el registro fallará al no encontrar el rol por defecto.</p>
     * 
     * <p>Resultado esperado: HTTP 201 Created y un JSON conteniendo un token JWT válido.</p>
     */
    @Test
    @DisplayName("POST /auth/register - Éxito: Registro y generación de Token")
    void register_DebeRetornar201YToken_CuandoCredencialesSonCorrectas() throws Exception {
        // --- ARRANGE (Preparación del estado de la BD y DTOs) ---
        
        // sembrar el rol obligatorio para el registro si no existe
        obtenerOSembrarRolUser();

        // Construir el payload JSON simulando los datos enviados por un Frontend
        RegisterRequestDTO requestDTO = new RegisterRequestDTO(
                "Test Integración", "tester99", 
                "tester@api.com", "password123"
        );

        // --- ACT & ASSERT (Llamada HTTP y aserciones sobre la respuesta) ---
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                
                // Verificaciones de la cabecera y el cuerpo HTTP
                .andExpect(status().isCreated()) // El estándar REST para creación es 201
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists()) // Validar que la respuesta contiene el JWT
                .andExpect(jsonPath("$.email").value("tester@api.com")); 
    }

    /**
     * Válida el endpoint de inicio de sesión de un usuario existente.
     * 
     * <p>Contexto: Para probar el login, primero debemos asegurarnos de que el usuario 
     * existe físicamente en la BD con su contraseña correctamente encriptada por Spring Security.
     * Por ello, simulamos primero un registro y luego el login.</p>
     */
    @Test
    @DisplayName("POST /auth/login - Éxito: Inicio de sesión correcto")
    void login_DebeRetornar200yToken_CuandoCredencialesSonCorrectas() throws Exception {
        // --- ARRANGE ---
        
        // 1. sembrar el rol si no existe
        obtenerOSembrarRolUser();

        // 2. Ejecutar un registro previo para "crear" al usuario en H2
        RegisterRequestDTO registerDTO = new RegisterRequestDTO(
                "login tester", "login_tester",
                "login@api.com", "mypassword"
        );
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)));
                
        // 3. Preparar las credenciales para el intento de login
        LoginRequestDTO loginDTO = new LoginRequestDTO("login@api.com", "mypassword");

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))

                // Verificamos que el login devuelve 200 OK y el JWT correspondiente
                .andExpect(status().isOk()) 
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists()) 
                .andExpect(jsonPath("$.email").value("login@api.com"));
    }

    /**
     * Válida la seguridad y el manejo de errores del endpoint de inicio de sesión.
     * 
     * <p>Contexto: Se simula un ataque o un error humano enviando una contraseña incorrecta.</p>
     * <p>Resultado esperado: El GlobalExceptionHandler debe atrapar el error interno y 
     * responder con un HTTP 409 Conflict y un mensaje claro para el cliente (ApiError JSON).</p>
     */
    @Test
    @DisplayName("POST /auth/login - Error: Credenciales Inválidas")
    void login_DebeRetornar409_CuandoCredencialesSonIncorrectas() throws Exception {
        // --- ARRANGE ---

        // 1. sembrar el rol si no existe
        obtenerOSembrarRolUser();

        // 2. Registrar al usuario en el sistema
        RegisterRequestDTO registroDTO = new RegisterRequestDTO(
                "Login Fail Tester", "login_fail",
                "fail@api.com", "password_correcta"
        );
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDTO)));

        // 3. Preparar el payload de login con la contraseña INTENCIONALMENTE incorrecta
        LoginRequestDTO loginFallidoDTO = new LoginRequestDTO("fail@api.com", "password_incorrecta");

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginFallidoDTO)))

                // Verificamos que el servidor protege la ruta y devuelve el error estandarizado
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    private Role obtenerOSembrarRolUser() {
        return roleRepository.findByName(RoleConstants.ROLE_USER)
                .orElseGet(() -> {
                    Role userRole = new Role();
                    userRole.setName(RoleConstants.ROLE_USER);
                    userRole.setDescription("Rol por defecto para pruebas de integración");
                    userRole.setCreatedAt(LocalDateTime.now());
                    return roleRepository.save(userRole);
                });
    }
}