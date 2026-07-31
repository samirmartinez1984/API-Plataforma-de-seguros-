package com.insurance_platform_springboot;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
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