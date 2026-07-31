package com.insurance_platform_springboot.model;

import com.insurance_platform_springboot.model.enums.ClaimStatus;
import com.insurance_platform_springboot.model.auth.User; // Importación corregida
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Entidad que representa un reclamo (claim) asociado a una póliza.
 * Incluye referencias al cliente, supervisor y partner, así como estado y fechas.
 */
@Entity
@Table(name = "claims")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    @ToString.Exclude
    private Policy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    private User customer; // Referencia corregida

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    @ToString.Exclude
    private User supervisor; // Referencia corregida

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    @ToString.Exclude
    private Partner partner;

    private String description;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @Schema(description = "Estado del reclamo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
