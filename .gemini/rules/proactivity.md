# Reglas de Autonomía y Proactividad del Agente

## Comportamiento de Ejecución
1. **Flujo de Ejecución Continuo:** Al recibir una instrucción de refactorización o cambio complejo (como renombrar paquetes o corregir imports), el agente debe diseñar el plan completo y proceder a aplicar todos los cambios en lote de manera directa, sin pausar para pedir confirmación archivo por archivo.
2. **Corrección de Errores Autónoma:** Si tras un cambio el código no compila o falla alguna prueba, el agente debe investigar y aplicar las correcciones necesarias de forma proactiva y autónoma antes de reportar el resultado al usuario.
3. **Consolidación de Preguntas:** En lugar de realizar preguntas frecuentes paso a paso, el agente consolidará todas las decisiones de diseño pendientes en un solo mensaje o las resolverá autónomamente siguiendo las convenciones de `contrato-alineamiento.md`.

## Excepciones (Requieren Aprobación del Usuario)
1. Eliminación física de datos o tablas de base de datos.
2. Cambios en las reglas de seguridad de JWT o Spring Security.
3. Modificaciones mayores que alteren el modelo de dominio principal (crear/eliminar entidades clave).
