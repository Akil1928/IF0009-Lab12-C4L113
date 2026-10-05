# IF0009-Lab12-C4L113 – TechConf: Asistentes, FormGroup anidado y depuración de recursión JSON

**Carnet:** C4L113
**Curso:** IF0009 – Desarrollo de Software IV · UCR Sede del Atlántico, Recinto Paraíso · II-2026
**Laboratorio 12:** Proyecto Full-Stack Autónomo (Expansión TechConf)

## Estructura
- `techconf-backend/`  → Spring Boot 3 + JPA + H2 + Bean Validation (puerto 8080)
- `techconf-frontend/` → Angular 18 standalone + Reactive Forms (puerto 4200)
- `docs/`              → evidencias (captura `error_recursion.png`)

## Ejecución
```bash
# Back-End (Java 17+, Maven)
cd techconf-backend && mvn spring-boot:run
# Front-End (Node 18+)
cd techconf-frontend && npm install && npx ng serve
```
- App: http://localhost:4200
- API: http://localhost:8080/api/charlas
- Consola H2: http://localhost:8080/h2-console (`jdbc:h2:mem:techconfdb`, `sa` / `password`)

## Funcionalidad implementada

### Back-End
| Requerimiento | Implementación |
|---|---|
| Entidad `Asistente` | `models/Asistente.java`: `id`, `nombreCompleto`, `correo`, `edad` |
| Relación bidireccional | `Charla` `@OneToMany(mappedBy="charla")` ↔ `Asistente` `@ManyToOne` + `@JoinColumn(name="charla_id")` |
| `data.sql` | 6 asistentes repartidos entre las 3 charlas |
| Endpoint | `POST /api/charlas/{id}/asistentes` → 201 con el asistente; 404 si no existe la charla; 409 si el correo ya está inscrito en esa charla |
| Validación en servidor | Bean Validation (`@NotBlank`, `@Size`, `@Email`, `@Min(18)`, `@NotNull`) + `GlobalExceptionHandler` que devuelve 400 con el detalle por campo |

### Front-End
- Botón **Inscribir Asistente** en cada tarjeta que despliega `AsistenteFormComponent`.
- Formulario anidado: `FormGroup` raíz que contiene el `FormGroup` `asistente`
  (`formGroupName="asistente"` en la plantilla).
  - `nombre`: requerido, mínimo 3 caracteres.
  - `correo`: requerido, formato email.
  - `edad`: requerida + **validador personalizado** `edadMinima(18)` (`validators/edad-minima.validator.ts`).
- Al enviar: `POST` al nuevo endpoint y la tarjeta se actualiza con el asistente devuelto.
- Los errores del servidor (400/404/409) se muestran en el formulario.
- Se conservan el `FormArray` de etiquetas y el validador cruzado de fechas de la práctica 11.b.

---

## Parte 2 – Depuración del error de recursión infinita

### 1. El problema
Con una relación bidireccional, Jackson serializa así:

```
Charla.asistentes → Asistente.charla → Charla.asistentes → Asistente.charla → ...
```

Nunca termina de recorrer el grafo y acaba lanzando un `StackOverflowError`, envuelto por Spring en:

```
org.springframework.http.converter.HttpMessageNotWritableException:
Could not write JSON: Infinite recursion (StackOverflowError)
  (through reference chain: com.techconf.models.Charla["asistentes"]->...Asistente["charla"]->...Charla["asistentes"]->...)
```

El navegador recibe un **HTTP 500** al hacer `GET /api/charlas`.

### 2. Cómo se provocó (reproducción)
1. En `Asistente.java` se **comentó** la anotación `@JsonIgnore` sobre el atributo `charla`.
2. Se reinició Spring Boot y se consultó `http://localhost:8080/api/charlas`.
3. Resultado: error 500 en el navegador y el stack trace en la consola de Spring Boot.
4. Evidencia: `docs/error_recursion.png`.

### 3. Solución aplicada
1. Se **restauró** `@JsonIgnore` en el lado "muchos" (`Asistente.charla`):

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "charla_id")
@JsonIgnore          // <- corta el ciclo: al serializar un Asistente NO se escribe su Charla
private Charla charla;
```

2. Se dejó la lista `asistentes` visible en el lado "uno" (`Charla`), de modo que el JSON de una charla incluya a sus asistentes, y cada asistente se serializa **sin** volver a referenciar su charla:

```json
{ "id": 1, "titulo": "...", "asistentes": [ { "id": 1, "nombreCompleto": "Ana Solano Vargas", "correo": "...", "edad": 21 } ] }
```

3. En `Charla.asistentes` se usó `@JsonProperty(access = READ_ONLY)` para que la lista se envíe en las respuestas pero se ignore en el JSON de entrada del `POST /api/charlas`.
4. Con `@ManyToOne(fetch = LAZY)` Hibernate pone un *proxy* en `charla`; `@JsonIgnore` evita además que Jackson intente serializar ese proxy.

### 4. Directivas de Jackson utilizadas
| Directiva | Dónde | Para qué |
|---|---|---|
| `@JsonIgnore` | `Asistente.charla` | Excluye el atributo de la serialización/deserialización y rompe el ciclo |
| `@JsonProperty(access = READ_ONLY)` | `Charla.asistentes` | La lista solo sale en las respuestas; no se acepta en la entrada |
| `@JsonIgnore` | `Charla.isRangoFechasValido()` | Evita que el método de validación se serialice como propiedad |

Alternativa equivalente (no usada): `@JsonManagedReference` en `Charla.asistentes` + `@JsonBackReference` en `Asistente.charla`.

### 5. Verificación
`GET /api/charlas` devuelve 200 con cada charla y su arreglo `asistentes`, y el `POST /api/charlas/{id}/asistentes` devuelve el asistente creado sin recursión.

## Control de versiones
Commits atómicos con prefijos `feat`, `chore` y `docs`. Ver `git log --oneline`.
