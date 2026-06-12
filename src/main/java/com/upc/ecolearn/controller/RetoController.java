package com.upc.ecolearn.controller;

import com.upc.ecolearn.model.Reto;
import com.upc.ecolearn.model.RetoUsuario;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.RetoService;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retos")
public class RetoController {

    @Autowired private RetoService retoService;
    @Autowired private UsuarioService usuarioService;

    // GET /api/retos — retos activos
    @GetMapping
    public ResponseEntity<List<Reto>> activos() {
        return ResponseEntity.ok(retoService.obtenerActivos());
    }

    // GET /api/retos/mis-retos — retos del usuario autenticado
    @GetMapping("/mis-retos")
    public ResponseEntity<List<RetoUsuario>> misRetos(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(retoService.obtenerRetosUsuario(usuario.getId()));
    }

    // POST /api/retos/{retoId}/inscribirse
    @PostMapping("/{retoId}/inscribirse")
    public ResponseEntity<RetoUsuario> inscribirse(
            @PathVariable String retoId,
            Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(retoService.inscribirse(usuario.getId(), retoId));
    }

    // ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Reto> crear(@RequestBody Reto reto) {
        return ResponseEntity.ok(retoService.crear(reto));
    }

    @PutMapping("/{retoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Reto> actualizar(
            @PathVariable String retoId,
            @RequestBody Reto reto) {
        return ResponseEntity.ok(retoService.actualizar(retoId, reto));
    }

    @DeleteMapping("/{retoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable String retoId) {
        retoService.eliminar(retoId);
        return ResponseEntity.noContent().build();
    }
}