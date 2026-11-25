package com.example.ejemplospring.controller;

import com.example.ejemplospring.dto.BeneficioDTO;
import com.example.ejemplospring.service.BeneficioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para el módulo de Beneficios.
 * Todas las respuestas son JSON mediante ResponseEntity.
 */
@RestController
@RequestMapping("/api/beneficios")
public class BeneficioController {

    // ── Inyección de la INTERFAZ (nunca la clase Impl) ────────────────────────
    private final BeneficioService beneficioService;

    public BeneficioController(BeneficioService beneficioService) {
        this.beneficioService = beneficioService;
    }

    // ── POST /api/beneficios  →  Crear beneficio individual ──────────────────
    /**
     * Recibe un JSON con los datos del beneficio.
     * @Valid activa las anotaciones de Jakarta Validation del DTO.
     */
    @PostMapping
    public ResponseEntity<BeneficioDTO> crear(@Valid @RequestBody BeneficioDTO dto) {
        BeneficioDTO creado = beneficioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // ── GET /api/beneficios  →  Listar todos ─────────────────────────────────
    @GetMapping
    public ResponseEntity<List<BeneficioDTO>> listar() {
        return ResponseEntity.ok(beneficioService.listarTodos());
    }

    // ── GET /api/beneficios/{id}  →  Detalle por ID ──────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<BeneficioDTO> obtener(@PathVariable int id) {
        return ResponseEntity.ok(beneficioService.obtenerPorId(id));
    }
}
