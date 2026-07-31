package com.insurance_platform_springboot.model;

import com.insurance_platform_springboot.model.enums.ProductStatus;
import com.insurance_platform_springboot.model.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un producto o servicio del catálogo comercializable.
 * Contiene información de precio base, cobertura, tipo y estado.
 */
@Entity
@Table(name = "catalog_products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class ProductCatalog { // catálogo de productos o servicios

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "name_product", nullable = false)
    private String nameProduct;

    private String description;
    private String coverage;
    private String exclusions;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Schema(description = "Tipo de producto del catálogo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    @Schema(description = "Estado del producto (activo/inactivo)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Policy> policies = new ArrayList<>();
}
