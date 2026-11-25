package com.example.ejemplospring.service;

import com.example.ejemplospring.dto.ConvocatoriaDTO;
import java.util.List;

/**
 * Contrato de negocio para el módulo de Convocatorias.
 */
public interface ConvocatoriaService {

    /** Crea una sola convocatoria, validando reglas de negocio. */
    ConvocatoriaDTO crear(ConvocatoriaDTO dto);

    /** Registra un lote de convocatorias en una sola transacción. */
    List<ConvocatoriaDTO> crearLote(List<ConvocatoriaDTO> lista);

    /** Retorna el detalle de una convocatoria por su ID. */
    ConvocatoriaDTO obtenerPorId(int id);

    /** Retorna todas las convocatorias. */
    List<ConvocatoriaDTO> listarTodas();

    /**
     * Filtra convocatorias por estado (obligatorio) y categoría del beneficio (opcional).
     */
    List<ConvocatoriaDTO> filtrar(String estado, String categoria);
}
