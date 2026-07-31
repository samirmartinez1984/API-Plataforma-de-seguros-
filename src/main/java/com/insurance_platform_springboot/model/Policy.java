package com.insurance_platform_springboot.model;

import com.insurance_platform_springboot.model.enums.PolicyStatus;
import com.insurance_platform_springboot.model.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una póliza de seguro contratada por un usuario.
 * Referencia al cliente, producto, fechas de vigencia, precio final y estado.
 */
@Entity
@Table(name = "policies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private ProductCatalog product;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status;

    @Column(name = "final_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal finalPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Claim> claims = new ArrayList<>();

    // Nuevos campos
    @Column(name = "base_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "extra_charges", precision = 10, scale = 2)
    private BigDecimal extraCharges;
}
