package com.upc.ecolearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chatbot_historial")
public class ChatbotHistorial {

    @Id
    private String id;

    private String usuarioId;

    private List<Mensaje> mensajes = new ArrayList<>();

    private LocalDateTime fechaInicio;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mensaje {
        // usuario o chatbot
        private String rol;
        private String contenido;
        private LocalDateTime fecha;
    }
}