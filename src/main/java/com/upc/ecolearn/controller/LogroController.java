package com.upc.ecolearn.controller;

import com.upc.ecolearn.model.Logro;
import com.upc.ecolearn.model.LogroUsuario;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.LogroService;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logros")
public class LogroController {

    @Autowired private LogroService logroService;
    @Autowired private UsuarioService usuarioService;

    // GET /api/logros — catálogo completo
    @GetMapping
    public ResponseEntity<List<Logro>> todos() {
        return ResponseEntity.ok(logroService.obtenerTodos());
    }

    // GET /api/logros/mis-logros — logros del usuario autenticado
    @GetMapping("/mis-logros")
    public ResponseEntity<List<LogroUsuario>> misLogros(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(logroService.obtenerLogrosUsuario(usuario.getId()));
    }

    // POST /api/logros — solo ADMIN crea logros
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Logro> crear(@RequestBody Logro logro) {
        return ResponseEntity.ok(logroService.crear(logro));
    }
}