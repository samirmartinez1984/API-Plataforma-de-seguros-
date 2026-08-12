package com.insurance_platform_springboot.serviceTest;

import com.insurance_platform_springboot.dtos.authDTO.AuthResponseDTO;
import com.insurance_platform_springboot.dtos.authDTO.LoginRequestDTO;
import com.insurance_platform_springboot.dtos.authDTO.RegisterRequestDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.mapper.UserMapper;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.repository.RoleRepository;
import com.insurance_platform_springboot.repository.UserRepository;
import com.insurance_platform_springboot.security.JwtTokenProvider;
import com.insurance_platform_springboot.security.UserDetailsServiceImpl;
import com.insurance_platform_springboot.service.NotificationService;
import com.insurance_platform_springboot.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el servicio de gestión de usuarios (UserService).
 * 
 * <p>Utiliza Mockito para simular todas las dependencias externas (repositorios, mappers, seguridad)
 * permitiendo validar la lógica de negocio de forma aislada y eficiente.</p>
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    /** Simulación del repositorio de usuarios en la base de datos. */
    @Mock
    private UserRepository userRepository;

    /** Simulación del repositorio de roles para la asignación de permisos. */
    @Mock
    private RoleRepository roleRepository;

    /** Simulación del mapper para conversión de DTOs y Entidades de usuario. */
    @Mock
    private UserMapper userMapper;

    /** Simulación del codificador de contraseñas de Spring Security (Bcrypt). */
    @Mock
    private PasswordEncoder passwordEncoder;

    /** Simulación del proveedor de tokens JWT para la generación de sesiones. */
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    /** Simulación del servicio encargado de cargar los detalles técnicos del usuario. */
    @Mock
    private UserDetailsServiceImpl userDetailsService;

    /** Simulación del gestor de autenticación de Spring Security para el login. */
    @Mock
    private AuthenticationManager authenticationManager;

    /** Simulacion del gestor de dependencia de spring mail.*/
    @Mock
    private NotificationService notificationService;

    /** Instancia real del servicio UserService con todos los mocks inyectados. */
    @InjectMocks
    private UserService userService;

    /**
     * Prueba el flujo exitoso de registro de un nuevo usuario en el sistema.
     * 
     * <p>Escenario: El usuario proporciona datos de registro válidos y únicos.</p>
     * <p>Verifica: Que se asigne el rol USER, se cifre la clave y se devuelva un token válido.</p>
     */
    @Test
    @DisplayName("Registrar Usuario - Éxito: Datos Correctos")
    void registrarUsuario_CuandoDatosSonCorrectos_DebeRetornarRespuestaExitosa(){
        
        // ARRANQUE (Preparación de la escena)
        RegisterRequestDTO requestDTO = new RegisterRequestDTO(
                "Samir Martinez", "Samir84", "samir@example.com", "password");

        Role mockRole = new Role();
        mockRole.setName(RoleConstants.ROLE_USER);

        User mockUser = new User();
        mockUser.setEmail(requestDTO.getEmail());
        mockUser.setRole(mockRole);
        
        UserDetails mockUserDetails = mock(UserDetails.class);
        String mockToken = "token-jwt-simulado";

        // Programar el comportamiento de los Mocks
        when(userRepository.existsByUsername(requestDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(roleRepository.findByName(RoleConstants.ROLE_USER)).thenReturn(Optional.of(mockRole));
        when(userMapper.registerToEntity(requestDTO)).thenReturn(mockUser);
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("password-encriptada");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userDetailsService.loadUserByUsername(requestDTO.getEmail())).thenReturn(mockUserDetails);
        when(jwtTokenProvider.generateToken(mockUserDetails)).thenReturn(mockToken);
        
        // ACT (Acción)
        AuthResponseDTO responseDTO = userService.register(requestDTO);
        
        // ASSERT (Verificación)
        assertNotNull(responseDTO, "La respuesta de registro no debería ser nula");
        assertEquals(mockToken, responseDTO.token(), "El token devuelto no coincide con el generado");
        
        // Confirmar que el usuario fue persistido físicamente en el repositorio
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Prueba el flujo de error al registrar un usuario cuyo email ya se encuentra en uso.
     * 
     * <p>Resultado esperado: Lanzamiento de una ConflictException y cancelación del guardado.</p>
     */
    @Test
    @DisplayName("Registrar Usuario - Error: Email duplicado")
    void registrarUsuario_CuandoEmailYaExiste_DebeLanzarExcepcionDeConflicto() {
        // ARRANQUE
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setEmail("duplicado@test.com");

        // Configuramos el mock para que indique que el email ya existe
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // ACT & ASSERT
        assertThrows(ConflictException.class, () -> userService.register(requestDTO),
                "El sistema debe bloquear el registro si el email ya está en la BD");
        
        // Verificación crítica: Aseguramos que nunca se llamó al método save para proteger la BD
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Prueba el flujo exitoso de autenticación de un usuario (Login).
     * 
     * <p>Escenario: El usuario proporciona credenciales de acceso correctas.</p>
     * <p>Verifica: La correcta interacción con el AuthenticationManager y el retorno del JWT.</p>
     */
    @Test
    @DisplayName("Login - Éxito: Credenciales Válidas")
    void iniciarSesion_CuandoCredencialesSonValidas_DebeRetornarToken() {
        // ARRANQUE
        LoginRequestDTO loginDTO = new LoginRequestDTO("samir@example.com", "password");

        Role mockRole = new Role();
        mockRole.setName(RoleConstants.ROLE_USER);

        User mockUser = new User();
        mockUser.setEmail(loginDTO.getEmail());
        mockUser.setRole(mockRole);
        
        UserDetails mockUserDetails = mock(UserDetails.class);
        String mockToken = "token-jwt-exitoso";

        // Programar Mocks para el flujo de login
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(userDetailsService.loadUserByUsername(loginDTO.getEmail())).thenReturn(mockUserDetails);
        when(jwtTokenProvider.generateToken(mockUserDetails)).thenReturn(mockToken);

        // ACT
        AuthResponseDTO response = userService.login(loginDTO);

        // ASSERT
        assertNotNull(response, "La respuesta de login no debería ser nula");
        assertEquals(mockToken, response.token(), "El token de sesión no es el esperado");
        verify(userRepository, times(1)).findByEmail(loginDTO.getEmail());
    }

    /**
     * Prueba el flujo de error en inicio de sesión por credenciales incorrectas.
     * 
     * <p>Escenario: El usuario proporciona datos de acceso inválidos.</p>
     * <p>Nota: Válida que el servicio transforme el error de seguridad interno en una excepción de negocio clara.</p>
     */
    @Test
    @DisplayName("Login - Error: Credenciales Inválidas")
    void iniciarSesion_CuandoCredencialesSonInvalidas_DebeLanzarExcepcion() {
        // ARRANQUE
        LoginRequestDTO loginDTO = new LoginRequestDTO("error@test.com", "wrong-password");

        // simulamos el fallo de autenticación de Spring Security
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        // ACT & ASSERT
        assertThrows(ConflictException.class, () -> userService.login(loginDTO),
                "El servicio debe lanzar ConflictException cuando la seguridad falla");

        // Verificar que no se procedió a generar el token tras el fallo de seguridad
        verify(jwtTokenProvider, never()).generateToken(any(UserDetails.class));
        verify(userRepository, never()).findByEmail(loginDTO.getEmail());
    }
}