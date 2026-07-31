package com.insurance_platform_springboot.repository;

import com.insurance_platform_springboot.model.Partner;
import com.insurance_platform_springboot.model.enums.PartnerStatus;
import com.insurance_platform_springboot.model.enums.PartnerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para el repositorio de Aliados (PartnerRepository).
 * 
 * <p>Válida que la red de proveedores (talleres, clínicas, etc.) se persista
 * correctamente y que las consultas de búsqueda por email, nombre y tipo 
 * sean precisas contra la base de datos H2.</p>
 */
public class PartnerRepositoryIntegracionTest extends BaseRepositoryIntegracionTest {

    /** Instancia real del repositorio de aliados. */
    @Autowired
    private PartnerRepository partnerRepository;

    /**
     * Válida que un aliado estratégico se guarde físicamente con sus datos de contacto.
     * 
     * <p>Escenario: Registro de un nuevo taller mecánico en el sistema.</p>
     * <p>Resultado esperado: Recuperación exitosa del objeto con su ID generado.</p>
     */
    @Test
    @DisplayName("PartnerRepository: Guardar y Buscar por ID - Éxito")
    void saveAndFindById_DebePersistirPartnerCorrectamente() {
        // --- ARRANGE ---
        Partner partner = crearPartner("Taller Solo Frenos", PartnerType.WORKSHOP, PartnerStatus.ACTIVE, "frenos@test.com");

        // --- ACT ---
        Partner guardado = partnerRepository.save(partner);
        Optional<Partner> encontrado = partnerRepository.findById(guardado.getId());

        // --- ASSERT ---
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getPartnerName()).isEqualTo("Taller Solo Frenos");
    }

    /**
     * Válida la búsqueda de aliados por su correo electrónico único.
     * <p>Uso en el sistema: Validación de duplicados durante el registro.</p>
     */
    @Test
    @DisplayName("PartnerRepository: Buscar por Email - Éxito")
    void findByEmail_DebeRetornarPartner_CuandoExiste() {
        // --- ARRANGE ---
        partnerRepository.save(crearPartner("Clínica Test", PartnerType.CLINIC, PartnerStatus.ACTIVE, "clinica@test.com"));

        // --- ACT ---
        Optional<Partner> encontrado = partnerRepository.findByEmail("clinica@test.com");

        // --- ASSERT ---
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("clinica@test.com");
    }

    /**
     * Válida el filtrado de aliados según su especialidad (Tipo).
     * <p>Uso en el sistema: Asignación de talleres o clínicas según el tipo de siniestro.</p>
     */
    @Test
    @DisplayName("PartnerRepository: Buscar por Tipo - Éxito")
    void findByType_DebeRetornarAliadosDelMismoTipo() {
        // --- ARRANGE ---
        partnerRepository.save(crearPartner("Taller A", PartnerType.WORKSHOP, PartnerStatus.ACTIVE, "a@t.com"));
        partnerRepository.save(crearPartner("Clínica B", PartnerType.CLINIC, PartnerStatus.ACTIVE, "b@c.com"));

        // --- ACT ---
        List<Partner> talleres = partnerRepository.findByType(PartnerType.WORKSHOP);

        // --- ASSERT ---
        assertThat(talleres).hasSize(1);
        assertThat(talleres.get(0).getType()).isEqualTo(PartnerType.WORKSHOP);
    }

    /**
     * Válida el mecanismo de integridad para evitar nombres de aliados duplicados.
     */
    @Test
    @DisplayName("PartnerRepository: Verificar existencia por nombre - Éxito")
    void existsByPartnerName_DebeRetornarTrue_CuandoExiste() {
        // --- ARRANGE ---
        String nombre = "Proveedor Único";
        partnerRepository.save(crearPartner(nombre, PartnerType.LABORATORY, PartnerStatus.ACTIVE, "lab@test.com"));

        // --- ACT & ASSERT ---
        assertThat(partnerRepository.existsByPartnerName(nombre)).isTrue();
        assertThat(partnerRepository.existsByPartnerName("No existe")).isFalse();
    }
}