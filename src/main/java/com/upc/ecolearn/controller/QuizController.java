package com.upc.ecolearn.controller;

import com.upc.ecolearn.model.Pregunta;
import com.upc.ecolearn.model.QuizUsuario;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.service.QuizService;
import com.upc.ecolearn.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired private QuizService quizService;
    @Autowired private UsuarioService usuarioService;

    // GET /api/quiz/preguntas?categoria=plastico  (categoría es opcional)
    @GetMapping("/preguntas")
    public ResponseEntity<List<Pregunta>> obtenerPreguntas(
            @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(quizService.obtenerPreguntas(categoria));
    }

    // POST /api/quiz/responder
    // Body: [ { "preguntaId": "...", "respuesta": "..." }, ... ]
    @PostMapping("/responder")
    public ResponseEntity<QuizUsuario> responder(
            @RequestBody List<Map<String, String>> respuestas,
            Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(quizService.enviarRespuestas(usuario.getId(), respuestas));
    }

    // GET /api/quiz/historial
    @GetMapping("/historial")
    public ResponseEntity<List<QuizUsuario>> historial(Authentication auth) {
        Usuario usuario = usuarioService.findByEmail(auth.getName());
        return ResponseEntity.ok(quizService.historialUsuario(usuario.getId()));
    }
}