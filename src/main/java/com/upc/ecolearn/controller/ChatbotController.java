package com.upc.ecolearn.controller;

import com.upc.ecolearn.model.ChatbotHistorial;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.ChatbotService;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired private ChatbotService chatbotService;
    @Autowired private UsuarioService usuarioService;

    // POST /api/chatbot/mensaje — body: {"mensaje": "¿Dónde va el plástico?"}
    @PostMapping("/mensaje")
    public ResponseEntity<ChatbotHistorial> enviarMensaje(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(chatbotService.enviarMensaje(usuario.getId(), body.get("mensaje")));
    }

    // POST /api/chatbot/nueva-sesion — inicia conversación nueva
    @PostMapping("/nueva-sesion")
    public ResponseEntity<ChatbotHistorial> nuevaSesion(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(chatbotService.nuevaSesion(usuario.getId()));
    }

    // GET /api/chatbot/historial — todas las sesiones del usuario
    @GetMapping("/historial")
    public ResponseEntity<List<ChatbotHistorial>> historial(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(chatbotService.obtenerHistorial(usuario.getId()));
    }
}