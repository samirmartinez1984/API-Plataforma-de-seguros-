package com.insurance_platform_springboot.controller;

import com.insurance_platform_springboot.dtos.request.PolicyRequestDTO;
import com.insurance_platform_springboot.dtos.update.PolicyUpdateDTO;
import com.insurance_platform_springboot.model.Policy;
import com.insurance_platform_springboot.model.ProductCatalog;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.model.enums.PolicyStatus;
import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import com.insurance_platform_springboot.repository.PolicyRepository;
import com.insurance_platform_springboot.repository.ProductCatalogRepository;
import com.insurance_platform_springboot.repository.RoleRepository;
import com.insurance_platform_springboot.repository.UserRepository;
import com.insurance_platform_springboot.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para el controlador de Pólizas (PolicyController).
 * Válida los flujos principales de emisión, consulta por usuario autenticado,
 * control de accesos de seguridad (ADMIN vs USER), actualización y baja lógica.
 */
public class PolicyControllerTest extends BaseControllerTest {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductCatalogRepository productCatalogRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Limpia las tablas involucradas antes de cada test para asegurar el aislamiento.
     */
    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
        productCatalogRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    /**
     * Válida que se pueda emitir una nueva póliza asociando un cliente y producto reales.
     */
    @Test
    @DisplayName("POST /policies - Éxito: Emisión de una nueva póliza")
    @WithMockUser(roles = "ADMIN")
    void crearPoliza_CuandoDatosSonCorrectos_DebeRetornar201Created() throws Exception {
        // ARRANGE
        Role userRole = crearRol(RoleConstants.ROLE_USER);
        User customer = crearUsuario("cliente@test.com", userRole);
        ProductCatalog product = crearProducto();

        PolicyRequestDTO requestDTO = new PolicyRequestDTO(
                customer.getId(),
                product.getId(),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                new BigDecimal("100.00"),
                0,
                BigDecimal.ZERO
        );

        // ACT & ASSERT
        mockMvc.perform(post("/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.finalPrice").value(100.00));
    }

    /**
     * Válida que un cliente pueda consultar únicamente sus pólizas personales.
     */
    @Test
    @DisplayName("GET /policies/my-policies - Éxito: Cliente consulta sus pólizas personales")
    void obtenerMisPolizas_CuandoUsuarioEstaAutenticado_DebeRetornar200Ok() throws Exception {
        // ARRANGE
        Role userRole = crearRol(RoleConstants.ROLE_USER);
        User customer = crearUsuario("mi_cliente@test.com", userRole);
        ProductCatalog product = crearProducto();
        crearPoliza(customer, product);

        CustomUserDetails userDetails = new CustomUserDetails(
                customer.getId(),
                customer.getEmail(),
                customer.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(RoleConstants.ROLE_USER))
        );

        // ACT & ASSERT
        mockMvc.perform(get("/policies/my-policies")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].customerId").value(customer.getId()));
    }

    /**
     * Válida que la seguridad prevenga que un cliente consulte el listado administrativo general.
     */
    @Test
    @DisplayName("GET /policies - Error: Cliente bloqueado al consultar listado general")
    @WithMockUser(roles = "USER")
    void listarTodasLasPolizas_CuandoUsuarioEsCliente_DebeRetornar403Forbidden() throws Exception {
        mockMvc.perform(get("/policies"))
                .andExpect(status().isForbidden());
    }

    /**
     * Válida que un Administrador pueda actualizar los detalles de una póliza existente.
     */
    @Test
    @DisplayName("PUT /policies/{id} - Éxito: Admin actualiza información de una póliza")
    @WithMockUser(roles = "ADMIN")
    void actualizarPoliza_CuandoUsuarioEsAdmin_DebeRetornar200Ok() throws Exception {
        // ARRANGE
        Role userRole = crearRol(RoleConstants.ROLE_USER);
        User customer = crearUsuario("cliente_update@test.com", userRole);
        ProductCatalog product = crearProducto();
        Policy policy = crearPoliza(customer, product);

        PolicyUpdateDTO updateDTO = new PolicyUpdateDTO(
                LocalDate.now().plusYears(2),
                "ACTIVE",
                new BigDecimal("150.00")
        );

        // ACT & ASSERT
        mockMvc.perform(put("/policies/" + policy.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.finalPrice").value(150.00));
    }

    /**
     * Válida que un Administrador pueda realizar la baja lógica de una póliza sin siniestros.
     */
    @Test
    @DisplayName("DELETE /policies/{id} - Éxito: Admin realiza baja lógica de una póliza")
    @WithMockUser(roles = "ADMIN")
    void eliminarPoliza_CuandoUsuarioEsAdmin_DebeRetornar204NoContent() throws Exception {
        // ARRANGE
        Role userRole = crearRol(RoleConstants.ROLE_USER);
        User customer = crearUsuario("cliente_delete@test.com", userRole);
        ProductCatalog product = crearProducto();
        Policy policy = crearPoliza(customer, product);

        // ACT & ASSERT
        mockMvc.perform(delete("/policies/" + policy.getId()))
                .andExpect(status().isNoContent());
    }

    // Métodos Helper para sembrar datos en H2

    private Role crearRol(String nombreRol) {
        Role role = new Role();
        role.setName(nombreRol);
        role.setDescription("Rol para pruebas de integración");
        role.setCreatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }

    private User crearUsuario(String email, Role role) {
        User user = new User();
        user.setName("Usuario Test");
        user.setUsername(email);
        user.setEmail(email);
        user.setPasswordHash("hashed_password");
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private ProductCatalog crearProducto() {
        ProductCatalog product = new ProductCatalog();
        product.setNameProduct("seguro de Auto");
        product.setDescription("Cobertura total contra robos");
        product.setBasePrice(new BigDecimal("100.00"));
        product.setType(ProductType.AUTO);
        product.setStatus(ProductStatus.ACTIVE);
        product.setCreatedAt(LocalDateTime.now());
        return productCatalogRepository.save(product);
    }

    private Policy crearPoliza(User customer, ProductCatalog product) {
        Policy policy = new Policy();
        policy.setCustomer(customer);
        policy.setProduct(product);
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusYears(1));
        policy.setBasePrice(product.getBasePrice());
        policy.setFinalPrice(product.getBasePrice());
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setCreatedAt(LocalDateTime.now());
        return policyRepository.save(policy);
    }
}
