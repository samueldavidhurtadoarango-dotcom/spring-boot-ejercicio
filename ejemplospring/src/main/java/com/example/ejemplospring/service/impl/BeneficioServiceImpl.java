package com.example.ejemplospring.service.impl;

import com.example.ejemplospring.dto.BeneficioDTO;
import com.example.ejemplospring.repository.BeneficioJdbcRepository;
import com.example.ejemplospring.service.BeneficioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementación del servicio de Beneficios.
 * Contiene la lógica de negocio y validaciones adicionales.
 */
@Service
@Transactional
public class BeneficioServiceImpl implements BeneficioService {

    private final BeneficioJdbcRepository beneficioRepo;

    // Inyección por constructor (buena práctica; facilita testing)
    public BeneficioServiceImpl(BeneficioJdbcRepository beneficioRepo) {
        this.beneficioRepo = beneficioRepo;
    }

    /**
     * Crea un beneficio validando que no exista otro con el mismo nombre
     * (ignorando mayúsculas y espacios extra).
     */
    @Override
    public BeneficioDTO crear(BeneficioDTO dto) {
        String nombreNormalizado = dto.getNombre().trim();
        if (beneficioRepo.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new IllegalArgumentException(
                    "Ya existe un beneficio con el nombre: '" + nombreNormalizado + "'");
        }
        return beneficioRepo.save(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficioDTO obtenerPorId(int id) {
        return beneficioRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Beneficio no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficioDTO> listarTodos() {
        return beneficioRepo.findAll();
    }
}
