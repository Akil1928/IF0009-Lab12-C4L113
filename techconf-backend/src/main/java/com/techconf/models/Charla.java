package com.techconf.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Charla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es requerido.")
    @Size(min = 5, message = "El título debe tener al menos 5 caracteres.")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "El expositor es requerido.")
    @Column(nullable = false)
    private String expositor;

    private String nivel; // Principiante, Intermedio, Avanzado

    @NotBlank(message = "El email de contacto es requerido.")
    @Email(message = "Formato de email inválido.")
    @Column(name = "email_contacto")
    private String emailContacto;

    @NotNull(message = "La fecha de inicio es requerida.")
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es requerida.")
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    // Colección embebida para almacenar múltiples etiquetas (tags)
    @NotEmpty(message = "Debe indicar al menos una etiqueta.")
    @ElementCollection
    @CollectionTable(name = "charla_etiquetas", joinColumns = @JoinColumn(name = "charla_id"))
    @Column(name = "etiqueta")
    private List<String> etiquetas = new ArrayList<>();

    // Lado "uno" de la relación 1:N. El dueño de la FK es Asistente.charla (mappedBy).
    // READ_ONLY: se serializa en las respuestas pero se ignora si llega en el JSON de entrada.
    @OneToMany(mappedBy = "charla", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Asistente> asistentes = new ArrayList<>();

    public Charla() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getExpositor() { return expositor; }
    public void setExpositor(String expositor) { this.expositor = expositor; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public List<String> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<String> etiquetas) { this.etiquetas = etiquetas; }

    public List<Asistente> getAsistentes() { return asistentes; }
    public void setAsistentes(List<Asistente> asistentes) { this.asistentes = asistentes; }

    // Validación cruzada en el servidor (equivalente al validador de Angular)
    @AssertTrue(message = "La fecha de fin no puede ser anterior a la de inicio.")
    @JsonIgnore
    public boolean isRangoFechasValido() {
        return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
    }

    // Mantiene sincronizados ambos lados de la relación bidireccional
    public void agregarAsistente(Asistente asistente) {
        asistentes.add(asistente);
        asistente.setCharla(this);
    }
}
