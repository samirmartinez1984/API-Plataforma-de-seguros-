package com.insurance_platform_springboot;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

/**
 * Clase principal que inicia la aplicación Spring Boot.
 *
 * <p>Anotaciones clave:</p>
 * <ul>
 *     <li>{@link SpringBootApplication}: Habilita la autoconfiguración de Spring Boot,
 *     el escaneo de componentes y la configuración de propiedades.</li>
 *     <li>{@link EnableAsync}: Activa las capacidades de ejecución de métodos asíncronos de Spring,
 *     fundamental para tareas en segundo plano como el envío de correos electrónicos.</li>
 * </ul>
 */
@SpringBootApplication
@EnableAsync
public class InsurancePlatformSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsurancePlatformSpringbootApplication.class, args);
    }

    /**
     * Configura la zona horaria predeterminada de la aplicación al arrancar.
     * Esto asegura que las fechas generadas con LocalDateTime coincidan con la hora local.
     */
    @PostConstruct
    public void init() {
        // Establecemos la zona horaria a Colombia/Perú/Ecuador (GMT-5)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        System.out.println("Zona horaria de la aplicación configurada a: " + TimeZone.getDefault().getID());
    }
}