package com.example.ejemplospring.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la entidad Convocatoria.
 * Usado tanto para peticiones (input) como para respuestas (output).
 */
public class ConvocatoriaDTO {

    // ── Campos de salida (read-only) ─────────────────────────────────────────
    private Integer idConvocatoria;
    private String  nombreBeneficio;   // Viene del JOIN con beneficios
    private LocalDateTime fechaCreacion;

    // ── Campos de entrada/salida ──────────────────────────────────────────────
    @NotNull(message = "El id_beneficio es obligatorio")
    private Integer idBeneficio;

    @NotBlank(message = "El nombre de la convocatoria es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de cierre es obligatoria")
    private LocalDate fechaCierre;

    @NotNull(message = "Los cupos son obligatorios")
    @Min(value = 1, message = "Los cupos deben ser al menos 1")
    private Integer cupos;

    @NotNull(message = "El valor de pago es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor de pago debe ser mayor a 0")
    private BigDecimal valorPago;

    private String estado; // 'Activa', 'Inactiva', 'Cerrada'

    // ── Constructor vacío ─────────────────────────────────────────────────────
    public ConvocatoriaDTO() {}

    // ── Getters y Setters ─────────────────────────────────────────────────────
    public Integer getIdConvocatoria() { return idConvocatoria; }
    public void setIdConvocatoria(Integer idConvocatoria) { this.idConvocatoria = idConvocatoria; }

    public Integer getIdBeneficio() { return idBeneficio; }
    public void setIdBeneficio(Integer idBeneficio) { this.idBeneficio = idBeneficio; }

    public String getNombreBeneficio() { return nombreBeneficio; }
    public void setNombreBeneficio(String nombreBeneficio) { this.nombreBeneficio = nombreBeneficio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDate fechaCierre) { this.fechaCierre = fechaCierre; }

    public Integer getCupos() { return cupos; }
    public void setCupos(Integer cupos) { this.cupos = cupos; }

    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
