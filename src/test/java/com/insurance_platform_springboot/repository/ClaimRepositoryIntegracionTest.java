package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.*;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.model.enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para el repositorio de Reclamaciones (ClaimRepository).
 * 
 * <p>Esta clase valida el escenario más complejo de persistencia del sistema, 
 * donde convergen Clientes, Pólizas, supervisores y Aliados de servicio en un mismo
 * registro de siniestro.</p>
 */
public class ClaimRepositoryIntegracionTest extends BaseRepositoryIntegracionTest {

    /** Instancia real del repositorio de reclamaciones. */
    @Autowired
    private ClaimRepository claimRepository;

    /** Repositorio de pólizas para vinculación y validación de coberturas. */
    @Autowired
    private PolicyRepository policyRepository;

    /** Repositorio de usuarios para la gestión de clientes y supervisores del caso. */
    @Autowired
    private UserRepository userRepository;

    /** Repositorio de roles necesario para la tipificación de usuarios. */
    @Autowired
    private RoleRepository roleRepository;

    /** Repositorio del catálogo para identificar el producto afectado. */
    @Autowired
    private ProductCatalogRepository productCatalogRepository;

    /** Repositorio de aliados estratégicos (talleres, clínicas) involucrados. */
    @Autowired
    private PartnerRepository partnerRepository;

    /**
     * Válida que un reclamo se guarde íntegramente con todas sus relaciones foráneas.
     * 
     * <p>Escenario: Creación de un siniestro asociando Póliza, Cliente, Supervisor y Partner.</p>
     * <p>Validación: Verifica que todos los ID de relación se mantengan persistidos correctamente.</p>
     */
    @Test
    @DisplayName("ClaimRepository: Guardar y Buscar por ID - Éxito")
    void saveAndFindById_DebePersistirReclamoCorrectamente() {
        // --- ARRANGE (Preparación del ecosistema) ---
        Role userRole = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        Role supervisorRole = roleRepository.save(crearRole(RoleConstants.ROLE_SUPERVISOR));

        User customer = userRepository.save(crearUser("Cliente Reclamos", "cliente-rec", "cliente@reclamos.com", userRole));
        User supervisor = userRepository.save(crearUser("Supervisor Reclamos", "super-rec", "supervisor@reclamos.com", supervisorRole));

        ProductCatalog product = productCatalogRepository.save(crearCatalogProduct("seguro Automotor",
                BigDecimal.valueOf(120.0), ProductType.AUTO, ProductStatus.ACTIVE));
        
        Policy policy = policyRepository.save(crearPolicy(customer, product, LocalDate.now(),
                LocalDate.now().plusYears(1), PolicyStatus.ACTIVE, BigDecimal.valueOf(120.0)));

        Partner partner = partnerRepository.save(crearPartner("Taller Autorizado S.A.",
                PartnerType.WORKSHOP, PartnerStatus.ACTIVE, "taller@rec.com"));

        Claim claim = crearClaim(policy, customer, supervisor, partner, ClaimStatus.REGISTERED);

        // --- ACT (Persistencia y recuperación) ---
        Claim guardado = claimRepository.save(claim);
        Claim encontrado = claimRepository.findById(guardado.getId()).orElse(null);

        // --- ASSERT ---
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getPolicy().getId()).isEqualTo(policy.getId());
        assertThat(encontrado.getSupervisor().getId()).isEqualTo(supervisor.getId());
        assertThat(encontrado.getPartner().getId()).isEqualTo(partner.getId());
    }

    /**
     * Válida la recuperación de reclamos asociados exclusivamente a un cliente.
     * 
     * <p>Uso en el sistema: Historial de siniestros personal del usuario.</p>
     * <p>Resultado esperado: Lista filtrada que contiene los reportes del titular indicado.</p>
     */
    @Test
    @DisplayName("ClaimRepository: Buscar por ID de Cliente - Éxito")
    void findByCustomerId_DebeRetornarReclamosDelCliente() {
        // --- ARRANGE ---
        Role role = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        User customer = userRepository.save(crearUser("Mario Test", "mariot", "mario@test.com", role));
        ProductCatalog prod = productCatalogRepository.save(crearCatalogProduct("Salud Dental",
                BigDecimal.valueOf(60.0), ProductType.HEALTH, ProductStatus.ACTIVE));
        Policy policy = policyRepository.save(crearPolicy(customer, prod, LocalDate.now(),
                LocalDate.now().plusYears(1), PolicyStatus.ACTIVE, BigDecimal.valueOf(60.0)));
        Partner partner = partnerRepository.save(crearPartner("Dental Plus", PartnerType.CLINIC,
                PartnerStatus.ACTIVE, "dental@plus.com"));

        claimRepository.save(crearClaim(policy, customer, customer, partner, ClaimStatus.REGISTERED));

        // --- ACT ---
        List<Claim> reclamos = claimRepository.findByCustomerId(customer.getId());

        // --- ASSERT ---
        assertThat(reclamos).hasSize(1);
        assertThat(reclamos.get(0).getCustomer().getId()).isEqualTo(customer.getId());
    }

    /**
     * Válida el filtrado de siniestros según su estado de gestión.
     * 
     * <p>Escenario: Búsqueda de casos con estado 'APPROVED' para procesos de indemnización.</p>
     */
    @Test
    @DisplayName("ClaimRepository: Buscar por Estado - Éxito")
    void findByStatus_DebeRetornarReclamosSegunEstado() {
        // --- ARRANGE ---
        Role role = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        User customer = userRepository.save(crearUser("Test Status", "status", "status@test.com", role));
        ProductCatalog prod = productCatalogRepository.save(crearCatalogProduct("seguro", BigDecimal.TEN, ProductType.LIFE, ProductStatus.ACTIVE));
        Policy pol = policyRepository.save(crearPolicy(customer, prod, LocalDate.now(), LocalDate.now().plusDays(1), PolicyStatus.ACTIVE, BigDecimal.TEN));
        Partner part = partnerRepository.save(crearPartner("Paz", PartnerType.CLINIC, PartnerStatus.ACTIVE, "p@p.com"));

        claimRepository.save(crearClaim(pol, customer, customer, part, ClaimStatus.APPROVED));

        // --- ACT ---
        List<Claim> aprobados = claimRepository.findByStatus(ClaimStatus.APPROVED);

        // --- ASSERT ---
        assertThat(aprobados).isNotEmpty();
        assertThat(aprobados.get(0).getStatus()).isEqualTo(ClaimStatus.APPROVED);
    }
}