# 🧪 Guía de Tests de Integración - Insurance Platform

## 📘 Propósito
Este documento define los **tests de integración esenciales** para el monolito de seguros.
Su objetivo es garantizar que las entidades, repositorios y relaciones funcionen correctamente con la base de datos real (H2 en entorno de pruebas).

---

## ⚙️ Configuración general
- **Perfil activo:** `test`
- **Base de datos:** H2 en memoria (`jdbc:h2:mem:testdb`)
- **Framework:** Spring Boot 3.x + JPA + Hibernate
- **Objetivo:** validar persistencia, relaciones y consultas reales.

---

## 🧩 1. UserRepositoryIntegraciónTest
**Objetivo:** validar persistencia y consulta de usuarios.

**Casos a probar:**
- Guardar un usuario con su rol asociado.
- Buscar por email y username.
- Verificar unicidad de email y username.
- Confirmar relación `User → Role`.

---

## 🧩 2. PolicyRepositoryIntegraciónTest
**Objetivo:** asegurar que las pólizas se crean y consultan correctamente.

**Casos a probar:**
- Persistir una póliza con `Customer` y `Product`.
- Consultar pólizas activas por cliente.
- Validar estados (`ACTIVE`, `EXPIRED`, etc.).
- Probar relaciones `Policy → User` y `Policy → CatalogProduct`.

---

## 🧩 3. ClaimRepositoryIntegraciónTest
**Objetivo:** verificar que los reclamos se guardan con todas sus relaciones.

**Casos a probar:**
- Crear un `Claim` con `Policy`, `Customer`, `Supervisor` y `Partner`.
- Consultar reclamos por estado (`REGISTERED`, `IN_REVIEW`, etc.).
- Validar integridad referencial (foreign keys).
- Probar actualización y eliminación de reclamos.

---

## 🧩 4. PartnerRepositoryIntegraciónTest
**Objetivo:** validar persistencia y consulta de aliados.

**Casos a probar:**
- Guardar un `Partner` con tipo (`CLINIC`, `WORKSHOP`, etc.).
- Consultar por estado (`ACTIVE`, `INACTIVE`).
- Verificar unicidad de `partner_name` y `email`.

---

## 🧩 5. CatalogProductRepositoryIntegraciónTest
**Objetivo:** asegurar que el catálogo de productos funciona correctamente.

**Casos a probar:**
- Guardar un producto con cobertura, exclusiones y tipo (`AUTO`, `HEALTH`, etc.).
- Consultar productos activos.
- Validar precios numéricos y decimales.
- Buscar por nombre de producto.

---

## 🧩 6. Relaciones cruzadas (Full Integration Flow)
**Objetivo:** probar que las entidades se integran correctamente entre sí.

**Flujo sugerido:**
1. Crear un `Role` y un `User`.
2. Crear un `CatalogProduct`.
3. Crear una `Policy` asociada al `User` y al `CatalogProduct`.
4. Crear un `Partner`.
5. Crear un `Claim` asociado a la `Policy`, `Partner` y `Supervisor`.

**Validaciones:**
- Todas las relaciones se guardan correctamente.
- Las consultas cruzadas devuelven resultados esperados.
- No hay errores de integridad referencial.

---

## 🧠 Recomendaciones
- Ejecutar estos tests en el pipeline de CI/CD después de los unitarios.
- Mantener los datos de prueba en `data.sql` o `@BeforeEach` para consistencia.
- Usar `@DataJpaTest` o `@SpringBootTest` según el alcance del test.
- Registrar logs de Hibernate para verificar las consultas generadas.

---

## ✅ Resultado esperado
- BD H2 inicializada correctamente.
- Inserciones y consultas exitosas.
- Relaciones entre entidades verificadas.
- **Exit code 0** al finalizar la ejecución.

---

> **Autor:** Samir Martínez
> **Proyecto:** Insurance Platform - Spring Boot
> **Última actualización:** Julio 2026
