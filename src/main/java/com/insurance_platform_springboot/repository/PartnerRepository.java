package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.enums.PartnerStatus;
import com.insurance_platform_springboot.model.enums.PartnerType;
import com.insurance_platform_springboot.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Partner.
 */
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    /** Buscar partners por tipo. */
    List<Partner> findByType(PartnerType type);

    /** Buscar partners por estado. */
    List<Partner> findByStatus(PartnerStatus status);

    /** Buscar partner por email (opcional). */
    Optional<Partner> findByEmail(String email);

    /** Buscar partners cuyo nombre contenga el texto, ignorando mayúsculas. */
    List<Partner> findByPartnerNameContainingIgnoreCase(String name);

    Boolean existsByPartnerName(String partnerName);



}
