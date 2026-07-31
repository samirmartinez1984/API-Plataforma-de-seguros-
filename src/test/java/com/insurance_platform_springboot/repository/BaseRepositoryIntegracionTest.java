package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.*;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.model.enums.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase base abstracta para centralizar la configuración de pruebas de integración de persistencia.
 * 
 * <p>Provee:
 * <ul>
 *   <li>Configuración automática de JPA y rollback de transacciones tras cada test.</li>
 *   <li>Uso del perfil de base de datos H2 (en memoria).</li>
 *   <li>Métodos utilitarios (Helpers) para la creación rápida de entidades consistentes.</li>
 * </ul>
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseRepositoryIntegracionTest {

    /**
     * Crea un objeto Role con metadatos básicos.
     * @param name Nombre del rol (ej.: ROLE_ADMIN).
     * @return Entidad Role preparada para persistir.
     */
    protected Role crearRole(String name) {
        Role role = new Role();
        role.setName(name);
        role.setDescription("Descripción administrativa del rol " + name);
        role.setCreatedAt(LocalDateTime.now());
        return role;
    }

    /**
     * Crea un usuario con credenciales de prueba y relación de rol establecida.
     * @return Entidad User completa.
     */
    protected User crearUser(String name, String username, String email, Role role) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("hash_de_seguridad_simulado");
        user.setEnabled(true);
        user.setRegisteredAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(role);
        return user;
    }

    /**
     * Construye un producto para el catálogo con sus atributos comerciales básicos.
     * @return Entidad CatalogProduct.
     */
    protected ProductCatalog crearCatalogProduct(String name, BigDecimal price, ProductType type, ProductStatus status) {
        ProductCatalog product = new ProductCatalog();
        product.setNameProduct(name);
        product.setDescription("Detalles comerciales de " + name);
        product.setCoverage("Cobertura estándar de mercado");
        product.setExclusions("Exclusiones por negligencia");
        product.setBasePrice(price);
        product.setType(type);
        product.setStatus(status);
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    /**
     * Crea un aliado estratégico (Partner) para la prestación de servicios.
     * @return Entidad Partner.
     */
    protected Partner crearPartner(String name, PartnerType type, PartnerStatus status, String email) {
        Partner partner = new Partner();
        partner.setPartnerName(name);
        partner.setType(type);
        partner.setStatus(status);
        partner.setEmail(email);
        partner.setAddress("Avenida Central 101");
        partner.setPhone("123-456-789");
        partner.setCreatedAt(LocalDateTime.now());
        return partner;
    }

    /**
     * Genera un contrato de póliza uniendo un cliente con un producto.
     * Realiza un cálculo de precio final simulado (90% del base).
     * @return Entidad Policy relacionada.
     */
    protected Policy crearPolicy(User customer, ProductCatalog product, LocalDate start, LocalDate end, PolicyStatus status, BigDecimal basePrice) {
        Policy policy = new Policy();
        policy.setCustomer(customer);
        policy.setProduct(product);
        policy.setStartDate(start);
        policy.setEndDate(end);
        policy.setStatus(status);
        policy.setBasePrice(basePrice);
        policy.setDiscountPercentage(10);
        policy.setExtraCharges(BigDecimal.ZERO);
        // Lógica de precio para el test: aplica un 10% de descuento fijo
        BigDecimal finalPrice = basePrice.multiply(BigDecimal.valueOf(0.9));
        policy.setFinalPrice(finalPrice);
        policy.setCreatedAt(LocalDateTime.now());
        return policy;
    }

    /**
     * Crea un siniestro (Claim) vinculando todas las entidades del ecosistema.
     * @return Entidad Claim con estado inicial.
     */
    protected Claim crearClaim(Policy policy, User customer, User supervisor, Partner partner, ClaimStatus status) {
        Claim claim = new Claim();
        claim.setPolicy(policy);
        claim.setCustomer(customer);
        claim.setSupervisor(supervisor);
        claim.setPartner(partner);
        claim.setDescription("Incidente reportado para validación de cobertura");
        claim.setReportedAt(LocalDateTime.now());
        claim.setStatus(status);
        claim.setCreatedAt(LocalDateTime.now());
        return claim;
    }
}