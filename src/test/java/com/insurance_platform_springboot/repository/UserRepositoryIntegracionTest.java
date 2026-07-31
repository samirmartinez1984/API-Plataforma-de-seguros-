package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.model.auth.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para el repositorio de Usuarios (UserRepository).
 * 
 * <p>Valida la persistencia real de las cuentas de usuario y la integridad de sus 
 * credenciales únicas en la base de datos de pruebas H2.</p>
 */
public class UserRepositoryIntegracionTest extends BaseRepositoryIntegracionTest {

    /** Instancia real del repositorio inyectada por Spring Data JPA. */
    @Autowired
    private UserRepository userRepository;

    /** Repositorio de roles necesario para establecer la relación jerárquica del usuario. */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Válida que un usuario pueda ser recuperado mediante su correo electrónico único.
     * 
     * <p>Escenario: Se inserta un registro real en H2 y se consulta por el campo 'email'.</p>
     * <p>Resultado esperado: Se retorna un Optional conteniendo el objeto User completo con sus relaciones.</p>
     */
    @Test
    @DisplayName("UserRepository: Buscar por Email - Éxito")
    void findByEmail_DebeRetornarUsuario_CuandoEmailExiste() {
        // --- ARRANGE ---
        Role role = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        User user = crearUser("Integración test", "int-test", "integracion@test.com", role);
        userRepository.save(user);

        // --- ACT ---
        Optional<User> encontrado = userRepository.findByEmail("integracion@test.com");

        // --- ASSERT ---
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("integracion@test.com");
    }

    /**
     * Válida el mecanismo de seguridad que impide el registro de correos electrónicos duplicados.
     * 
     * <p>Escenario: Se verifica la existencia de un email que ya reside en la base de datos.</p>
     * <p>Resultado esperado: Retorna true para el existente y false para uno nuevo.</p>
     */
    @Test
    @DisplayName("UserRepository: Verificar existencia por Email")
    void existsByEmail_DebeRetornarTrue_CuandoEmailExiste() {
        // --- ARRANGE ---
        Role role = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        userRepository.save(crearUser("Email Test", "email-test", "exist-email@test.com", role));

        // --- ACT & ASSERT ---
        assertThat(userRepository.existsByEmail("exist-email@test.com")).isTrue();
        assertThat(userRepository.existsByEmail("inexistente@test.com")).isFalse();
    }

    /**
     * Válida el mecanismo de seguridad que impide el registro de nombres de usuario duplicados.
     * 
     * <p>Escenario: El sistema consulta si un 'username' ya está tomado por otro cliente.</p>
     * <p>Resultado esperado: Retorna true si el nombre de usuario ya está persistido.</p>
     */
    @Test
    @DisplayName("UserRepository: Verificar existencia por Username")
    void existsByUsername_DebeRetornarTrue_CuandoUsernameExiste() {
        // --- ARRANGE ---
        Role role = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        userRepository.save(crearUser("User Test", "username-unique", "unique@test.com", role));

        // --- ACT & ASSERT ---
        assertThat(userRepository.existsByUsername("username-unique")).isTrue();
        assertThat(userRepository.existsByUsername("no-existe")).isFalse();
    }
}