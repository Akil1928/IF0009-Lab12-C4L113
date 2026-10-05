IF0009-Lab12-C4L113 – TechConf: Asistentes, FormGroup anidado y depuración de recursión JSON

Carnet: C4L113 Curso: IF0009 – Desarrollo de Software IV · UCR Sede del Atlántico, Recinto Paraíso · II-2026 Laboratorio 12: Proyecto Full-Stack Autónomo (Expansión TechConf)

Estructura
techconf-backend/ → Spring Boot 3 + JPA + H2 + Validación de Beans (puerto 8080)
techconf-frontend/→ Angular 18 independiente + Formularios reactivos (puerto 4200)
docs/ → evidencias de la Parte 2 ( error_recursion.pngy error_recursion_navegador.png)
Ejecución
intento
# Back-End (Java 17+, Maven)
cd techconf-backend && mvn spring-boot:run
# Front-End (Node 18+)
cd techconf-frontend && npm install && npx ng serve
Aplicación: http://localhost:4200
API: http://localhost:8080/api/charlas
Consola H2: http://localhost:8080/h2-console ( jdbc:h2:mem:techconfdb, sa/ password)
Funcionalidad implementada
Back-end
Requerimiento	Implementación
EntidadAsistente	models/Asistente.java: id, nombreCompleto, correo,edad
Relación bidireccional	Charla @OneToMany(mappedBy="charla")↔ Asistente @ManyToOne+@JoinColumn(name="charla_id")
data.sql	6 asistentes repartidos entre las 3 charlas
Punto final	POST /api/charlas/{id}/asistentes→ 201 con el asistente; 404 si no existe la charla; 409 si el correo ya está inscrito en esa charla
Validación en servidor	Bean Validation ( @NotBlank, @Size, @Email, @Min(18), @NotNull) + GlobalExceptionHandlerque devuelve 400 con el detalle por campo
Interfaz
Botón Inscribir Asistente en cada tarjeta que despliega AsistenteFormComponent.
Formulario anidado: FormGroupraíz que contiene el FormGroup asistente ( formGroupName="asistente"en la plantilla).
nombre: requerido, mínimo 3 caracteres.
correo: requerido, formato email.
edad: requerido + validador personalizado edadMinima(18) ( validators/edad-minima.validator.ts).
Al enviar: POSTal nuevo endpoint y la tarjeta se actualiza con el asistente devuelto.
Los errores del servidor (400/404/409) se muestran en el formulario.
Se conservan el FormArrayde etiquetas y el validador cruzado de fechas de la práctica 11.b.
Parte 2 – Depuración del error de recursión infinita
1. El problema

Con una relación bidireccional, Jackson serializa así:

Charla.asistentes → Asistente.charla → Charla.asistentes → Asistente.charla → ...

Nunca termine de recorrer el grafo. En la versión de Jackson utilizada en este proyecto, el ciclo se corta al superar el límite de 1000 niveles de anidamiento y Spring lanza:

org.springframework.http.converter.HttpMessageNotWritableException:
Could not write JSON: Document nesting depth (1001) exceeds the maximum allowed
(1000, from `StreamWriteConstraints.getMaxNestingDepth()`)

En versiones anteriores de Jackson (< 2.15) el mismo ciclo termina en StackOverflowError; en versiones recientes Jackson corta la recursión al superar los 1000 niveles de anidamiento y lanza la excepción de arriba.

En el navegador no aparece una página de error 500: la respuesta ya había comenzado a enviarse cuando Jackson falló, por lo que el navegador recibe un JSON infinito y truncado que repite el ciclo charla → asistentes → charla → .... En la consola de Spring Boot queda el HttpMessageNotWritableException.

2. Cómo se produce (reproducción)
En Asistente.javase comenta la anotación @JsonIgnoresobre el atributo charla.
Se reinició Spring Boot y se consultó http://localhost:8080/api/charlas.
Resultado: el navegador muestra el JSON infinito truncado y la consola de Spring Boot muestra la excepción.
Evidencia:

Consola de Spring Boot ( docs/error_recursion.png):

Mostrar imagen

Navegador ( docs/error_recursion_navegador.png):

Mostrar imagen

3. Solución aplicada
Se restauró @JsonIgnore en el lado "muchos" ( Asistente.charla):
Java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "charla_id")
@JsonIgnore          // <- corta el ciclo: al serializar un Asistente NO se escribe su Charla
private Charla charla;
Se dejó la lista asistentesvisible en el lado "uno" ( Charla), de modo que el JSON de una charla incluya a sus asistentes, y cada asistente se serializa sin volver a referenciar su charla:
json
{ "id": 1, "titulo": "...", "asistentes": [ { "id": 1, "nombreCompleto": "Ana Solano Vargas", "correo": "...", "edad": 21 } ] }
En Charla.asistentesse nosotros @JsonProperty(access = READ_ONLY)para que la lista se envíe en las respuestas pero se ignore en el JSON de entrada del POST /api/charlas.
Con @ManyToOne(fetch = LAZY)Hibernate pone un proxy en charla; @JsonIgnoreEvite además que Jackson intente serializar ese proxy.
4. Directivas de Jackson utilizadas
Directiva	Dónde	Para qué
@JsonIgnore	Asistente.charla	Excluye el atributo de la serialización/deserialización y rompe el ciclo
@JsonProperty(access = READ_ONLY)	Charla.asistentes	La lista solo sale en las respuestas; no se acepta en la entrada
@JsonIgnore	Charla.isRangoFechasValido()	Evite que el método de validación se serialice como propiedad

Alternativa equivalente (no usada): @JsonManagedReferenceen Charla.asistentes+ @JsonBackReferenceen Asistente.charla.

5. Verificación

Con @JsonIgnorerestaurado, GET /api/charlasdevuelve 200 con cada charla y su arreglo asistentes, y el POST /api/charlas/{id}/asistentesdevuelve el asistente creado sin recursión.

Control de versiones

Comete atómicos con prefijos feat, chorey docs. Ver git log --oneline.