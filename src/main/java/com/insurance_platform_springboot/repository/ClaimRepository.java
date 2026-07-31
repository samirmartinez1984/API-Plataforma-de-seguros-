package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.Claim;
import com.insurance_platform_springboot.model.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Claim (reclamos).
 */
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    /** Reclamos por ID de póliza. */
    List<Claim> findByPolicyId(Long policyId);

    /** Reclamos por ID de cliente. */
    List<Claim> findByCustomerId(Long customerId);

    /** Reclamos asignados a un supervisor (por ID). */
    List<Claim> findBySupervisorId(Long supervisorId);

    /** Reclamos relacionados con un partner (por ID). */
    List<Claim> findByPartnerId(Long partnerId);

    /** Buscar reclamos por estado (ClaimStatus). */
    List<Claim> findByStatus(ClaimStatus status);

    /** Buscar reclamos reportados entre dos fechas. */
    List<Claim> findByReportedAtBetween(LocalDateTime start, LocalDateTime end);
}
