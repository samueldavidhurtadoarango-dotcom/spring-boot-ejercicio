package com.example.ejemplospring.web;

import com.example.ejemplospring.dto.BeneficioDTO;
import com.example.ejemplospring.service.BeneficioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Web para las vistas Thymeleaf de Beneficios.
 * Separado del @RestController para mantener la API pura intacta.
 */
@Controller
@RequestMapping("/web/beneficios")
public class BeneficioWebController {

    private final BeneficioService beneficioService;

    public BeneficioWebController(BeneficioService beneficioService) {
        this.beneficioService = beneficioService;
    }

    // ── GET /web/beneficios  →  Lista de beneficios ───────────────────────────
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("beneficios", beneficioService.listarTodos());
        model.addAttribute("titulo", "Beneficios Registrados");
        return "beneficios/lista";
    }

    // ── GET /web/beneficios/nuevo  →  Formulario vacío ───────────────────────
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("beneficio", new BeneficioDTO());
        model.addAttribute("titulo", "Nuevo Beneficio");
        return "beneficios/formulario";
    }

    // ── POST /web/beneficios/nuevo  →  Guardar beneficio ─────────────────────
    @PostMapping("/nuevo")
    public String guardar(@Valid @ModelAttribute("beneficio") BeneficioDTO dto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Nuevo Beneficio");
            return "beneficios/formulario";
        }
        try {
            beneficioService.crear(dto);
            flash.addFlashAttribute("exito", "Beneficio creado exitosamente");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("titulo", "Nuevo Beneficio");
            return "beneficios/formulario";
        }
        return "redirect:/web/beneficios";
    }

    // ── GET /web/beneficios/{id}  →  Detalle de un beneficio ─────────────────
    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("beneficio", beneficioService.obtenerPorId(id));
        model.addAttribute("titulo", "Detalle del Beneficio");
        return "beneficios/detalle";
    }
}
