package com.upc.ecolearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "preguntas")
public class Pregunta {

    @Id
    private String id;

    private String pregunta;
    private String tipo; // "opcion_multiple", "verdadero_falso"
    private String categoria; // "plastico", "papel", "vidrio", "metal", "organico", "bateria", "ropa", "general"
    private List<String> opciones;
    private String respuestaCorrecta;
    private String explicacion; // feedback amigable para el niño
    private int xp; // puntos que da responder correctamente
}