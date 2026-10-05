# Práctica Guiada 11.b – FormArray, Validador Cruzado y @ElementCollection

**Carnet:** C4L113
**Curso:** IF0009 – Desarrollo de Software IV · UCR Sede del Atlántico, Recinto Paraíso · II-2026

Continuación de la Práctica 11.a.

## Estructura
- `techconf-backend/`  → Spring Boot 3 + JPA (`@ElementCollection`) + H2 (puerto 8080)
- `techconf-frontend/` → Angular 18 standalone: `FormArray` + validador cruzado de fechas (puerto 4200)

## Ejecución
```bash
# Back-End (Java 17+, Maven)
cd techconf-backend && mvn spring-boot:run
# Front-End (Node 18+)
cd techconf-frontend && npm install && npx ng serve
```
- Consola H2: http://localhost:8080/h2-console  (`jdbc:h2:mem:techconfdb`, `sa` / `password`)
- Tablas: `CHARLA` y `CHARLA_ETIQUETAS`
- App: http://localhost:4200

## Reto evaluativo
1. Fecha fin < fecha inicio → aparece el mensaje de error y "Guardar Charla" se deshabilita.
2. "+ Añadir Otra Etiqueta" genera campos dinámicos (ej. Docker, DevOps, CI/CD).
3. Al guardar, las etiquetas se persisten en `CHARLA_ETIQUETAS` y la tarjeta las muestra.

## Archivos nuevos/modificados
- Back-End: `Charla.java` (fechas + etiquetas), `data.sql`
- Front-End: `validators/rango-fechas.validator.ts`, `charla.service.ts`, `charla-registro.component.{ts,html,css}`

## Nota técnica
`application.properties` conserva `spring.jpa.defer-datasource-initialization=true`, necesario para que
`data.sql` corra después de que Hibernate cree `CHARLA` y `CHARLA_ETIQUETAS`.
Al guardar, el formulario se reinicia dejando un solo input de etiqueta (`FormArray.clear()` + `push`),
porque `reset()` no elimina los controles extra.
