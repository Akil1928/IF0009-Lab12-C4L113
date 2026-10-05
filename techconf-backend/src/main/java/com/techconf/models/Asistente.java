package com.techconf.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Asistente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre completo es requerido.")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres.")
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @NotBlank(message = "El correo es requerido.")
    @Email(message = "Formato de correo inválido.")
    @Column(nullable = false)
    private String correo;

    @NotNull(message = "La edad es requerida.")
    @Min(value = 18, message = "El asistente debe ser mayor de edad (18 años o más).")
    @Column(nullable = false)
    private Integer edad;

    // Lado "muchos" de la relación. @JsonIgnore evita la recursión infinita
    // Charla -> asistentes -> Asistente -> charla -> asistentes -> ...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charla_id")
    @JsonIgnore
    private Charla charla;

    public Asistente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public Charla getCharla() { return charla; }
    public void setCharla(Charla charla) { this.charla = charla; }
}
