-- Inserción de Charlas principales con fechas
INSERT INTO charla (titulo, expositor, nivel, email_contacto, fecha_inicio, fecha_fin)
VALUES ('Introduccion a la Inteligencia Artificial', 'Dra. Maria Rojas', 'Principiante', 'maria@ai-tech.cr', '2026-11-15', '2026-11-15');

INSERT INTO charla (titulo, expositor, nivel, email_contacto, fecha_inicio, fecha_fin)
VALUES ('Microservicios con Spring Cloud', 'Ing. Carlos Brenes', 'Avanzado', 'carlos@spring.io', '2026-11-16', '2026-11-17');

INSERT INTO charla (titulo, expositor, nivel, email_contacto, fecha_inicio, fecha_fin)
VALUES ('Angular 18: Señales y Standalone', 'Licda. Laura Gomez', 'Intermedio', 'laura@angular.dev', '2026-11-17', '2026-11-18');

-- Etiquetas (tabla generada por @ElementCollection). charla_id = 1, 2, 3 por ser IDENTITY
INSERT INTO charla_etiquetas (charla_id, etiqueta) VALUES (1, 'IA');
INSERT INTO charla_etiquetas (charla_id, etiqueta) VALUES (1, 'Machine Learning');
INSERT INTO charla_etiquetas (charla_id, etiqueta) VALUES (2, 'Spring Boot');
INSERT INTO charla_etiquetas (charla_id, etiqueta) VALUES (2, 'Backend');
INSERT INTO charla_etiquetas (charla_id, etiqueta) VALUES (2, 'Nube');
INSERT INTO charla_etiquetas (charla_id, etiqueta) VALUES (3, 'Angular');
INSERT INTO charla_etiquetas (charla_id, etiqueta) VALUES (3, 'Frontend');

-- Lab 12: Asistentes inscritos (charla_id = 1, 2, 3)
INSERT INTO asistente (nombre_completo, correo, edad, charla_id) VALUES ('Ana Solano Vargas', 'ana.solano@correo.cr', 21, 1);
INSERT INTO asistente (nombre_completo, correo, edad, charla_id) VALUES ('Luis Mora Chaves', 'luis.mora@correo.cr', 25, 1);
INSERT INTO asistente (nombre_completo, correo, edad, charla_id) VALUES ('Daniela Quesada Ruiz', 'daniela.quesada@correo.cr', 30, 2);
INSERT INTO asistente (nombre_completo, correo, edad, charla_id) VALUES ('Mario Jimenez Soto', 'mario.jimenez@correo.cr', 19, 2);
INSERT INTO asistente (nombre_completo, correo, edad, charla_id) VALUES ('Sofia Araya Lopez', 'sofia.araya@correo.cr', 22, 3);
INSERT INTO asistente (nombre_completo, correo, edad, charla_id) VALUES ('Jose Pablo Cordero', 'jose.cordero@correo.cr', 28, 3);
