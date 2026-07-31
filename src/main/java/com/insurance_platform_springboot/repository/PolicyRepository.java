package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.enums.PolicyStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import com.insurance_platform_springboot.model.Policy;
import com.insurance_platform_springboot.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Policy.
 */
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    /** Buscar pólizas por ID del cliente. */
    List<Policy> findByCustomerId(Long customerId);

    /** Buscar pólizas por estado (PolicyStatus). */
    List<Policy> findByStatus(PolicyStatus status);

    /** Buscar pólizas cuya fecha de fin sea anterior a la indicada. */
    List<Policy> findByEndDateBefore(LocalDate date);

    /** Buscar pólizas por tipo de producto asociado. */
    List<Policy> findByProductType(ProductType type);

    List<Policy> findByCustomer(User customer);

}
