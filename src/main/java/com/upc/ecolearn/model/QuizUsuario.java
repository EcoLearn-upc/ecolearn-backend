package com.upc.ecolearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quiz_usuario")
public class QuizUsuario {

    @Id
    private String id;

    private String usuarioId;
    private List<RespuestaQuiz> respuestas;
    private int puntosGanados;
    private int correctas;
    private int total;
    private LocalDateTime fecha;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RespuestaQuiz {
        private String preguntaId;
        private String respuestaUsuario;
        private boolean correcta;
    }
}