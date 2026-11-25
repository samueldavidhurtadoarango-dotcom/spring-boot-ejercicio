package com.example.ejemplospring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO para la entidad Beneficio.
 * Usado tanto para peticiones (input) como para respuestas (output).
 */
public class BeneficioDTO {

    // ── Campos de salida (read-only) ─────────────────────────────────────────
    private Integer idBeneficio;
    private LocalDateTime fechaCreacion;

    // ── Campos de entrada/salida ──────────────────────────────────────────────
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no puede superar 100 caracteres")
    private String categoria;

    private String estado; // 'Activo' | 'Inactivo'

    // ── Constructor vacío ─────────────────────────────────────────────────────
    public BeneficioDTO() {}

    // ── Getters y Setters ─────────────────────────────────────────────────────
    public Integer getIdBeneficio() { return idBeneficio; }
    public void setIdBeneficio(Integer idBeneficio) { this.idBeneficio = idBeneficio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
