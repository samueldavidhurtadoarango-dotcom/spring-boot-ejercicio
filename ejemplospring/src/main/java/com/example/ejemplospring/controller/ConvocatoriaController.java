package com.example.ejemplospring.controller;

import com.example.ejemplospring.dto.ConvocatoriaDTO;
import com.example.ejemplospring.service.ConvocatoriaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller para el módulo de Convocatorias.
 * Demuestra las 5 formas de recibir datos en Spring Boot:
 *   1. @RequestBody (JSON individual)
 *   2. @RequestBody List<> (lote JSON)
 *   3. @PathVariable (GET por ID)
 *   4. @RequestParam con @Validated (filtros query-string)
 *   5. MultipartFile (upload de archivo)
 */
@RestController
@RequestMapping("/api/convocatorias")
@Validated   // Habilita validación de @RequestParam a nivel de clase
public class ConvocatoriaController {

    // ── Inyección de la INTERFAZ (nunca la clase Impl) ────────────────────────
    private final ConvocatoriaService convocatoriaService;

    // Inyectado desde application.properties: app.uploads.dir
    private final String uploadsDir;

    public ConvocatoriaController(ConvocatoriaService convocatoriaService,
                                  @Value("${app.uploads.dir:uploads}") String uploadsDir) {
        this.convocatoriaService = convocatoriaService;
        this.uploadsDir = uploadsDir;
    }

    // ── 1. POST /api/convocatorias  →  Crear convocatoria individual (JSON) ───
    @PostMapping
    public ResponseEntity<ConvocatoriaDTO> crear(@Valid @RequestBody ConvocatoriaDTO dto) {
        ConvocatoriaDTO creada = convocatoriaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // ── 2. POST /api/convocatorias/lote  →  Lote JSON transaccional ───────────
    @PostMapping("/lote")
    public ResponseEntity<List<ConvocatoriaDTO>> crearLote(
            @RequestBody List<@Valid ConvocatoriaDTO> lista) {
        List<ConvocatoriaDTO> creadas = convocatoriaService.crearLote(lista);
        return ResponseEntity.status(HttpStatus.CREATED).body(creadas);
    }

    // ── 3a. GET /api/convocatorias  →  Listar todas ───────────────────────────
    @GetMapping
    public ResponseEntity<List<ConvocatoriaDTO>> listar() {
        return ResponseEntity.ok(convocatoriaService.listarTodas());
    }

    // ── 3b. GET /api/convocatorias/{id}  →  Detalle por @PathVariable ─────────
    @GetMapping("/{id}")
    public ResponseEntity<ConvocatoriaDTO> obtener(@PathVariable int id) {
        return ResponseEntity.ok(convocatoriaService.obtenerPorId(id));
    }

    // ── 4. GET /api/convocatorias/filtrar  →  @RequestParam con @Validated ────
    /**
     * Parámetros:
     *   - estado (obligatorio): 'Activa' | 'Inactiva' | 'Cerrada'
     *   - categoria (opcional): e.g. 'Educacion', 'Salud'
     *
     * @NotBlank en @RequestParam requiere @Validated a nivel de clase.
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<ConvocatoriaDTO>> filtrar(
            @RequestParam
            @NotBlank(message = "El parámetro 'estado' es obligatorio y no puede estar vacío")
            String estado,

            @RequestParam(required = false)
            String categoria) {

        return ResponseEntity.ok(convocatoriaService.filtrar(estado, categoria));
    }

    // ── 5. POST /api/convocatorias/{id}/soporte  →  MultipartFile upload ──────
    /**
     * Sube un archivo de imagen (image/*) como banner de la convocatoria.
     * - Valida que el Content-Type sea image/*
     * - Guarda con nombre único UUID en carpeta local `uploads/`
     * - Devuelve confirmación en JSON
     */
    @PostMapping(value = "/{id}/soporte", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> subirSoporte(
            @PathVariable int id,
            @RequestParam("archivo") MultipartFile archivo) throws IOException {

        // Validar que la convocatoria exista
        convocatoriaService.obtenerPorId(id); // lanza NoSuchElementException si no existe

        // Validar que el archivo sea una imagen
        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(Map.of(
                            "error", "Solo se permiten archivos de imagen (image/*)",
                            "tipoRecibido", contentType != null ? contentType : "desconocido"
                    ));
        }

        // Obtener extensión del archivo original
        String nombreOriginal = archivo.getOriginalFilename();
        String extension = "";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }

        // Generar nombre único con UUID
        String nombreUnico = UUID.randomUUID() + extension;

        // Crear directorio uploads/ si no existe
        Path dirPath = Paths.get(uploadsDir);
        Files.createDirectories(dirPath);

        // Guardar el archivo
        Path destino = dirPath.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        // Respuesta en JSON
        Map<String, Object> respuesta = new java.util.LinkedHashMap<>();
        respuesta.put("mensaje",        "Soporte subido exitosamente");
        respuesta.put("idConvocatoria", id);
        respuesta.put("nombreArchivo",  nombreUnico);
        respuesta.put("rutaRelativa",   uploadsDir + "/" + nombreUnico);
        respuesta.put("tamanoBytes",    archivo.getSize());
        respuesta.put("tipoContenido",  contentType);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
