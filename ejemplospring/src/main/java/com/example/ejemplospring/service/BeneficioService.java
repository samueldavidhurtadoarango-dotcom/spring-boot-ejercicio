package com.example.ejemplospring.service;

import com.example.ejemplospring.dto.BeneficioDTO;
import java.util.List;

/**
 * Contrato de negocio para el módulo de Beneficios.
 */
public interface BeneficioService {

    /** Crea un nuevo beneficio validando unicidad de nombre. */
    BeneficioDTO crear(BeneficioDTO dto);

    /** Retorna el detalle de un beneficio por su ID. */
    BeneficioDTO obtenerPorId(int id);

    /** Retorna todos los beneficios registrados. */
    List<BeneficioDTO> listarTodos();
}
