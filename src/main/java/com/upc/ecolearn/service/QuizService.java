package com.upc.ecolearn.service;

import com.upc.ecolearn.exception.EcoLearnException;
import com.upc.ecolearn.model.Pregunta;
import com.upc.ecolearn.model.QuizUsuario;
import com.upc.ecolearn.repository.PreguntaRepository;
import com.upc.ecolearn.repository.QuizUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired private PreguntaRepository preguntaRepository;
    @Autowired private QuizUsuarioRepository quizUsuarioRepository;
    @Autowired private UsuarioService usuarioService;

    // Devuelve 5 preguntas aleatorias (opcionalmente filtradas por categoría)
    public List<Pregunta> obtenerPreguntas(String categoria) {
        List<Pregunta> pool;
        if (categoria != null && !categoria.isBlank()) {
            pool = preguntaRepository.findAll().stream()
                    .filter(p -> categoria.equalsIgnoreCase(p.getCategoria()))
                    .toList();
        } else {
            pool = preguntaRepository.findAll();
        }

        if (pool.isEmpty()) {
            throw new EcoLearnException("No hay preguntas disponibles para esta categoría", HttpStatus.NOT_FOUND);
        }

        List<Pregunta> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled);
        return shuffled.stream().limit(5).toList();
    }

    // Recibe las respuestas del usuario, evalúa y guarda el resultado
    public QuizUsuario enviarRespuestas(String usuarioId, List<Map<String, String>> respuestas) {
        List<QuizUsuario.RespuestaQuiz> evaluadas = new ArrayList<>();
        int puntosGanados = 0;
        int correctas = 0;

        for (Map<String, String> r : respuestas) {
            String preguntaId = r.get("preguntaId");
            String respuestaUsuario = r.get("respuesta");

            Pregunta pregunta = preguntaRepository.findById(preguntaId)
                    .orElseThrow(() -> new EcoLearnException("Pregunta no encontrada", HttpStatus.NOT_FOUND));

            boolean esCorrecta = pregunta.getRespuestaCorrecta().equalsIgnoreCase(respuestaUsuario);
            if (esCorrecta) {
                puntosGanados += pregunta.getXp();
                correctas++;
            }

            evaluadas.add(new QuizUsuario.RespuestaQuiz(preguntaId, respuestaUsuario, esCorrecta));
        }

        // Sumar XP al usuario
        if (puntosGanados > 0) {
            usuarioService.agregarPuntos(usuarioId, puntosGanados);
        }

        // Guardar historial
        QuizUsuario registro = new QuizUsuario();
        registro.setUsuarioId(usuarioId);
        registro.setRespuestas(evaluadas);
        registro.setPuntosGanados(puntosGanados);
        registro.setCorrectas(correctas);
        registro.setTotal(respuestas.size());
        registro.setFecha(LocalDateTime.now());

        return quizUsuarioRepository.save(registro);
    }

    public List<QuizUsuario> historialUsuario(String usuarioId) {
        return quizUsuarioRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
}