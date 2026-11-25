package com.example.ejemplospring.service.impl;

import com.example.ejemplospring.dto.ConvocatoriaDTO;
import com.example.ejemplospring.repository.BeneficioJdbcRepository;
import com.example.ejemplospring.repository.ConvocatoriaJdbcRepository;
import com.example.ejemplospring.service.ConvocatoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementación del servicio de Convocatorias.
 * Contiene todas las reglas de negocio requeridas.
 */
@Service
@Transactional
public class ConvocatoriaServiceImpl implements ConvocatoriaService {

    private final ConvocatoriaJdbcRepository convocatoriaRepo;
    private final BeneficioJdbcRepository    beneficioRepo;

    public ConvocatoriaServiceImpl(ConvocatoriaJdbcRepository convocatoriaRepo,
                                   BeneficioJdbcRepository beneficioRepo) {
        this.convocatoriaRepo = convocatoriaRepo;
        this.beneficioRepo    = beneficioRepo;
    }

    /**
     * Crea una convocatoria aplicando todas las reglas de negocio.
     */
    @Override
    public ConvocatoriaDTO crear(ConvocatoriaDTO dto) {
        validarReglas(dto);
        return convocatoriaRepo.save(dto);
    }

    /**
     * Registra un lote en una sola transacción:
     * si cualquier elemento falla, toda la operación hace rollback.
     */
    @Override
    public List<ConvocatoriaDTO> crearLote(List<ConvocatoriaDTO> lista) {
        if (lista == null || lista.isEmpty()) {
            throw new IllegalArgumentException("La lista de convocatorias no puede estar vacía");
        }
        List<ConvocatoriaDTO> creadas = new ArrayList<>();
        for (ConvocatoriaDTO dto : lista) {
            validarReglas(dto);
            creadas.add(convocatoriaRepo.save(dto));
        }
        return creadas;
    }

    @Override
    @Transactional(readOnly = true)
    public ConvocatoriaDTO obtenerPorId(int id) {
        return convocatoriaRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Convocatoria no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConvocatoriaDTO> listarTodas() {
        return convocatoriaRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConvocatoriaDTO> filtrar(String estado, String categoria) {
        return convocatoriaRepo.findByEstadoAndCategoria(estado, categoria);
    }

    // ── Método privado de validación de reglas de negocio ─────────────────────
    private void validarReglas(ConvocatoriaDTO dto) {
        // Regla 1: el beneficio padre debe existir y estar Activo
        if (!beneficioRepo.existsActivoById(dto.getIdBeneficio())) {
            throw new IllegalArgumentException(
                    "El beneficio con id " + dto.getIdBeneficio()
                    + " no existe o no está en estado 'Activo'");
        }

        // Regla 2: fecha_cierre debe ser posterior a fecha_inicio
        if (!dto.getFechaCierre().isAfter(dto.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha de cierre debe ser posterior a la fecha de inicio");
        }
    }
}
