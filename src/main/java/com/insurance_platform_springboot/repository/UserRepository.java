package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad User. Provee métodos de consulta usando
 * la convención de nombres de Spring Data JPA.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** Buscar un usuario por su email. Retorna Optional vacío si no existe. */
    Optional<User> findByEmail(String email);

    /** Verificar si existe un usuario con el email indicado. */
    boolean existsByEmail(String email);

    /** Verificar si existe un usuario con el username indicado. */
    boolean existsByUsername(String username);
}
