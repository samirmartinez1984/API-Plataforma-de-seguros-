package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad Role.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /** Buscar rol por nombre. */
    Optional<Role> findByName(String name);
}
