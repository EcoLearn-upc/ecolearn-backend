package com.upc.ecolearn.controller;

import com.upc.ecolearn.model.Clase;
import com.upc.ecolearn.model.MetricaAula;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.ClaseService;
import com.upc.ecolearn.service.MetricaAulaService;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    @Autowired private ClaseService claseService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private MetricaAulaService metricaAulaService;

    @PostMapping
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<Clase> crear(@RequestBody Map<String, String> body, Authentication auth) {
        Usuario docente = usuarioService.findByEmail(auth.getName());
        Clase clase = claseService.crearClase(
                docente.getId(),
                body.get("nombre"),
                body.get("grado"),
                body.get("seccion"),
                body.get("colegio"),
                body.get("codigoAcceso")
        );
        return ResponseEntity.ok(clase);
    }

    @PostMapping("/{claseId}/alumnos")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<List<Usuario>> agregarAlumnos(
            @PathVariable String claseId,
            @RequestBody Map<String, List<String>> body) {
        return ResponseEntity.ok(claseService.agregarAlumnos(claseId, body.get("nombres")));
    }

    @GetMapping("/mis-clases")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<List<Clase>> misClases(Authentication auth) {
        Usuario docente = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(claseService.obtenerClasesDocente(docente.getId()));
    }

    @GetMapping("/codigo/{codigoAcceso}")
    public ResponseEntity<Map<String, Object>> obtenerPorCodigo(@PathVariable String codigoAcceso) {
        return ResponseEntity.ok(claseService.obtenerInfoPublica(codigoAcceso));
    }

    @GetMapping("/codigo/{codigoAcceso}/detalle")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<Map<String, Object>> obtenerDetallePorCodigo(@PathVariable String codigoAcceso) {
        return ResponseEntity.ok(claseService.obtenerDetallePorCodigo(codigoAcceso));
    }

    @GetMapping("/codigo/{codigoAcceso}/metricas")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<MetricaAula> obtenerMetricas(
            @PathVariable String codigoAcceso) {
        Clase clase = claseService.findByCodigoAcceso(codigoAcceso);
        MetricaAula metrica = metricaAulaService.obtenerMetricaActual(
                clase.getColegio(), clase.getGrado(), clase.getSeccion());
        return ResponseEntity.ok(metrica != null ? metrica : new MetricaAula());
    }

    @GetMapping("/{claseId}/alumnos")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<List<Usuario>> alumnos(@PathVariable String claseId) {
        return ResponseEntity.ok(claseService.obtenerAlumnos(claseId));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginEstudiante(@RequestBody Map<String, String> body) {
        String token = claseService.loginEstudiante(
                body.get("codigoAcceso"),
                body.get("nombre"),
                body.get("pin")
        );
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/mi-clase")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> miClase(Authentication auth) {
        Usuario alumno = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(claseService.obtenerMiClase(alumno.getId()));
    }
}