package com.upc.ecolearn.controller;

import com.upc.ecolearn.dto.ResiduoResponse;
import com.upc.ecolearn.model.Residuo;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.ResiduoService;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/residuos")
public class ResiduoController {

    @Autowired private ResiduoService residuoService;
    @Autowired private UsuarioService usuarioService;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping(value = "/clasificar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResiduoResponse> clasificar(
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

    @GetMapping("/warmup")
    public ResponseEntity<String> warmup() {
        try {
            restTemplate.getForEntity(aiServiceUrl + "/health", String.class);
            return ResponseEntity.ok("AI Service despertado");
        } catch (Exception e) {
            return ResponseEntity.ok("AI Service despertando...");
        }
    }
}