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
