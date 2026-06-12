package com.upc.ecolearn.controller;

import com.upc.ecolearn.model.MetricaAula;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.MetricaAulaService;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metricas")
public class MetricaAulaController {

    @Autowired private MetricaAulaService metricaAulaService;
    @Autowired private UsuarioService usuarioService;

    // GET /api/metricas/mi-aula — métricas del aula del docente/estudiante autenticado
    @GetMapping("/mi-aula")
    public ResponseEntity<List<MetricaAula>> miAula(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(metricaAulaService.obtenerPorAula(
                usuario.getColegio(), usuario.getGrado(), usuario.getSeccion()));
    }

    // GET /api/metricas/colegio — solo DOCENTE o ADMIN
    @GetMapping("/colegio")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ADMIN')")
    public ResponseEntity<List<MetricaAula>> porColegio(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(metricaAulaService.obtenerPorColegio(usuario.getColegio()));
    }
}