package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.model.Policy;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.model.enums.PolicyStatus;
import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para el repositorio de Pólizas (PolicyRepository).
 * 
 * <p>Válida la persistencia real de los contratos de seguros, asegurando que las relaciones
 * complejas con Clientes y Productos del Catálogo se mantengan íntegras en el motor de base de datos.</p>
 */
public class PolicyRepositoryIntegracionTest extends BaseRepositoryIntegracionTest {

    /** Instancia real del repositorio de pólizas. */
    @Autowired
    private PolicyRepository policyRepository;

    /** Repositorio de usuarios para la gestión de titulares de pólizas. */
    @Autowired
    private UserRepository userRepository;

    /** Repositorio de roles para la jerarquía de usuarios. */
    @Autowired
    private RoleRepository roleRepository;

    /** Repositorio del catálogo de productos de seguros. */
    @Autowired
    private ProductCatalogRepository productCatalogRepository;

    /**
     * Válida que una póliza se guarde físicamente con sus relaciones y cálculos de precio asociados.
     * 
     * <p>Escenario: Creación de un contrato uniendo un usuario real con un producto del catálogo.</p>
     * <p>Validación: Verifica que el ID sea generado y que el titular sea el correcto.</p>
     */
    @Test
    @DisplayName("PolicyRepository: Guardar y Buscar por ID - Éxito")
    void saveAndFindById_DebePersistirPolizaCorrectamente() {
        // --- ARRANGE ---
        Role role = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        User customer = userRepository.save(crearUser("Juan Pérez", "juanp", "juan@test.com", role));
        ProductCatalog product = productCatalogRepository.save(crearCatalogProduct(
                "seguro de Auto", BigDecimal.valueOf(100.0), ProductType.AUTO, ProductStatus.ACTIVE));

        Policy policy = crearPolicy(customer, product, LocalDate.now(), LocalDate.now().plusYears(1), PolicyStatus.ACTIVE, BigDecimal.valueOf(100.0));

        // --- ACT ---
        Policy guardada = policyRepository.save(policy);
        Policy encontrada = policyRepository.findById(guardada.getId()).orElse(null);

        // --- ASSERT ---
        assertThat(encontrada).isNotNull();
        assertThat(encontrada.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(encontrada.getProduct().getId()).isEqualTo(product.getId());
        assertThat(encontrada.getStatus()).isEqualTo(PolicyStatus.ACTIVE);
    }

    /**
     * Valida la recuperación de pólizas filtradas por el titular.
     * 
     * <p>Uso en el sistema: Pantalla principal del cliente para ver sus contratos vigentes.</p>
     * <p>Resultado esperado: Lista de pólizas pertenecientes únicamente al ID proporcionado.</p>
     */
    @Test
    @DisplayName("PolicyRepository: Buscar por ID de Cliente - Éxito")
    void findByCustomerId_DebeRetornarPolizasDelCliente() {
        // --- ARRANGE ---
        Role role = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        User customer = userRepository.save(crearUser("Ana Gómez", "anag", "ana@test.com", role));
        ProductCatalog product = productCatalogRepository.save(crearCatalogProduct(
                "seguro Hogar", BigDecimal.valueOf(200.0), ProductType.HOME, ProductStatus.ACTIVE));

        policyRepository.save(crearPolicy(customer, product, LocalDate.now(), LocalDate.now().plusYears(1), PolicyStatus.ACTIVE, BigDecimal.valueOf(200.0)));

        // --- ACT ---
        List<Policy> polizas = policyRepository.findByCustomerId(customer.getId());

        // --- ASSERT ---
        assertThat(polizas).hasSize(1);
        assertThat(polizas.get(0).getCustomer().getName()).isEqualTo("Ana Gómez");
    }

    /**
     * Válida el filtrado de contratos según su estado operativo.
     * 
     * <p>Escenario: Búsqueda de todas las pólizas con estado 'ACTIVE'.</p>
     * <p>Validación: Asegura que la consulta SQL ignore pólizas expiradas o canceladas.</p>
     */
    @Test
    @DisplayName("PolicyRepository: Buscar por Estado de Póliza - Éxito")
    void findByStatus_DebeRetornarPolizasConEstado() {
        // --- ARRANGE ---
        Role role = roleRepository.save(crearRole(RoleConstants.ROLE_USER));
        User customer = userRepository.save(crearUser("Carlos Ruiz", "carlosr", "carlos@test.com", role));
        ProductCatalog product = productCatalogRepository.save(crearCatalogProduct(
                "seguro Vida", BigDecimal.valueOf(150.0), ProductType.LIFE, ProductStatus.ACTIVE));

        policyRepository.save(crearPolicy(customer, product, LocalDate.now(), LocalDate.now().plusYears(1), PolicyStatus.ACTIVE, BigDecimal.valueOf(150.0)));
        policyRepository.save(crearPolicy(customer, product, LocalDate.now().minusYears(1), LocalDate.now().minusDays(1), PolicyStatus.EXPIRED, BigDecimal.valueOf(150.0)));

        // --- ACT ---
        List<Policy> activas = policyRepository.findByStatus(PolicyStatus.ACTIVE);

        // --- ASSERT ---
        assertThat(activas).hasSize(1);
        assertThat(activas.get(0).getStatus()).isEqualTo(PolicyStatus.ACTIVE);
    }
}