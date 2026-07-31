package com.insurance_platform_springboot.model.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un rol de usuario (p. ej. ADMIN, USER, SUPERVISOR).
 * Cada rol puede estar asociado a múltiples usuarios.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<User> users = new ArrayList<>();
}
