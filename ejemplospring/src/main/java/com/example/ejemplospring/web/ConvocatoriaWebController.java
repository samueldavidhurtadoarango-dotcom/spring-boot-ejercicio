package com.example.ejemplospring.web;

import com.example.ejemplospring.dto.BeneficioDTO;
import com.example.ejemplospring.dto.ConvocatoriaDTO;
import com.example.ejemplospring.service.BeneficioService;
import com.example.ejemplospring.service.ConvocatoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador Web para las vistas Thymeleaf de Convocatorias.
 */
@Controller
@RequestMapping("/web/convocatorias")
public class ConvocatoriaWebController {

    private final ConvocatoriaService convocatoriaService;
    private final BeneficioService    beneficioService;

    public ConvocatoriaWebController(ConvocatoriaService convocatoriaService,
                                     BeneficioService beneficioService) {
        this.convocatoriaService = convocatoriaService;
        this.beneficioService    = beneficioService;
    }

    // ── GET /web/convocatorias  →  Lista de convocatorias ────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("convocatorias", convocatoriaService.listarTodas());
        model.addAttribute("titulo", "Convocatorias Registradas");
        return "convocatorias/lista";
    }

    // ── GET /web/convocatorias/nueva  →  Formulario vacío ────────────────────
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        List<BeneficioDTO> beneficiosActivos = beneficioService.listarTodos()
                .stream()
                .filter(b -> "Activo".equals(b.getEstado()))
                .toList();

        model.addAttribute("convocatoria", new ConvocatoriaDTO());
        model.addAttribute("beneficios", beneficiosActivos);
        model.addAttribute("titulo", "Nueva Convocatoria");
        return "convocatorias/formulario";
    }

    // ── POST /web/convocatorias/nueva  →  Guardar convocatoria ───────────────
    @PostMapping("/nueva")
    public String guardar(@Valid @ModelAttribute("convocatoria") ConvocatoriaDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("beneficios", beneficioService.listarTodos()
                    .stream().filter(b -> "Activo".equals(b.getEstado())).toList());
            model.addAttribute("titulo", "Nueva Convocatoria");
            return "convocatorias/formulario";
        }
        try {
            convocatoriaService.crear(dto);
            flash.addFlashAttribute("exito", "Convocatoria creada exitosamente");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("beneficios", beneficioService.listarTodos()
                    .stream().filter(b -> "Activo".equals(b.getEstado())).toList());
            model.addAttribute("titulo", "Nueva Convocatoria");
            return "convocatorias/formulario";
        }
        return "redirect:/web/convocatorias";
    }

    // ── GET /web/convocatorias/{id}  →  Detalle ───────────────────────────────
    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("convocatoria", convocatoriaService.obtenerPorId(id));
        model.addAttribute("titulo", "Detalle de la Convocatoria");
        return "convocatorias/detalle";
    }

    // ── GET /web/convocatorias/filtrar  →  Filtro por estado ─────────────────
    @GetMapping("/filtrar")
    public String filtrar(@RequestParam(required = false) String estado,
                          @RequestParam(required = false) String categoria,
                          Model model) {
        if (estado != null && !estado.isBlank()) {
            model.addAttribute("convocatorias", convocatoriaService.filtrar(estado, categoria));
        } else {
            model.addAttribute("convocatorias", convocatoriaService.listarTodas());
        }
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("categoriaFiltro", categoria);
        model.addAttribute("titulo", "Filtrar Convocatorias");
        return "convocatorias/lista";
    }
}
