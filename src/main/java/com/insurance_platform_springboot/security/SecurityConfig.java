package com.insurance_platform_springboot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase de configuración principal para Spring Security.
 *
 * <p>Define las reglas de acceso, el manejo de sesiones (stateless para JWT),
 * la exposición de endpoints de monitoreo y registra los filtros personalizados.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite el uso de anotaciones como @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Configuración de la cadena de filtros de seguridad HTTP.
     *
     * <p>Define qué rutas son públicas y cuáles requieren autenticación. Está configurada
     * para ser 'stateless', lo cual es ideal para una API REST que utiliza JWT.</p>
     *
     * <p><b>Reglas de Acceso:</b></p>
     * <ul>
     *     <li><b>Rutas Públicas (no requieren token):</b>
     *         <ul>
     *             <li><code>/auth/**</code>: Para registro e inicio de sesión.</li>
     *             <li><code>/v3/api-docs/**</code> y <code>/swagger-ui/**</code>: Para la documentación de la API (Swagger).</li>
     *             <li><code>/actuator/health</code> y <code>/actuator/info</code>: Endpoints de Spring Boot Actuator
     *             para que sistemas externos (como orquestadores de contenedores o balanceadores de carga)
     *             puedan verificar el estado y la información de la aplicación.</li>
     *         </ul>
     *     </li>
     *     <li><b>Rutas Protegidas:</b>
     *         <ul>
     *             <li>Cualquier otra ruta (<code>anyRequest()</code>) requiere un token JWT válido.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param http El objeto HttpSecurity para configurar la seguridad web.
     * @return La cadena de filtros de seguridad (SecurityFilterChain) configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF (no es necesario en APIs REST stateless con JWT)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No se crean sesiones de usuario en el servidor
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Añadir el filtro JWT antes del filtro de autenticación estándar
                .build();
    }

    /**
     * Define el proveedor de autenticación (AuthenticationProvider) que se conecta
     * con la base de datos a través de UserDetailsService.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Le dice a Spring cómo cargar los detalles del usuario
        authProvider.setPasswordEncoder(passwordEncoder()); // Le dice a Spring cómo verificar las contraseñas
        return authProvider;
    }

    /**
     * Define el bean para el codificador de contraseñas.
     * Se utiliza BCrypt, que es el estándar recomendado para el hashing de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el AuthenticationManager de Spring Security como un bean.
     * Es necesario para poder inyectarlo y usarlo en el proceso de login manual.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}