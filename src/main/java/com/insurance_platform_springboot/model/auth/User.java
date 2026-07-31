package com.insurance_platform_springboot.model.auth;

import com.insurance_platform_springboot.model.Claim;
import com.insurance_platform_springboot.model.Policy;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un usuario del sistema.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(length = 150)
    private String name;

    @Column(unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @ToString.Exclude
    private Role role;

    private Boolean enabled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Policy> policies = new ArrayList<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Claim> claimsCustomer = new ArrayList<>();

    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Claim> claimsSupervisor = new ArrayList<>();
}
