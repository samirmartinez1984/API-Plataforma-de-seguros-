package com.insurance_platform_springboot.model;

import com.insurance_platform_springboot.model.enums.PartnerStatus;
import com.insurance_platform_springboot.model.enums.PartnerType;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un socio/proveedor (partner) del sistema.
 * Contiene datos de contacto, tipo y estado.
 */
@Entity
@Table(name = "partners")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    @Schema(description = "Tipo de partner/proveedor")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerType type;

    private String address;
    private String phone;
    private String email;

    @Schema(description = "Estado del partner (ACTIVE/INACTIVE)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "partner", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Claim> claims = new ArrayList<>();
}
