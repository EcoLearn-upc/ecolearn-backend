package com.upc.ecolearn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResiduoResponse {

    private String id;
    private String usuarioId;
    private String imagenUrl;
    private String categoriaDetectada;
    private double confianza;
    private boolean esCorrecta;
    private int puntosGanados;
    private LocalDateTime fecha;
}