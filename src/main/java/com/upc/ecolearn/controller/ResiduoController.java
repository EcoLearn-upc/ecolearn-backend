package com.upc.ecolearn.controller;

import com.upc.ecolearn.model.Residuo;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.ResiduoService;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/residuos")
public class ResiduoController {

    @Autowired private ResiduoService residuoService;
    @Autowired private UsuarioService usuarioService;

    @PostMapping(value = "/clasificar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Residuo> clasificar(
            @RequestParam("imagen") MultipartFile imagen,
            Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(residuoService.clasificar(usuario.getId(), imagen));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<Residuo>> historial(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(residuoService.historialUsuario(usuario.getId()));
    }

    @GetMapping("/imagen/{gridFsId}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable String gridFsId) throws Exception {
        InputStream stream = residuoService.obtenerImagen(gridFsId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(stream.readAllBytes());
    }
}