package com.upc.ecolearn.controller;

import com.upc.ecolearn.dto.UsuarioPerfilResponse;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired private UsuarioService usuarioService;

    // GET /api/usuarios/perfil
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilResponse> perfil(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(usuarioService.obtenerPerfil(usuario.getId()));
    }

    // GET /api/usuarios/ranking
    @GetMapping("/ranking")
    public ResponseEntity<List<Usuario>> ranking() {
        return ResponseEntity.ok(usuarioService.obtenerRanking());
    }
}