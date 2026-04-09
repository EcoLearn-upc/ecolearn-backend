package com.upc.ecolearn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricaAulaResponse {

    private String colegio;
    private String grado;
    private String seccion;
    private int totalClasificaciones;
    private int totalPuntos;
    private int retosCompletados;
    private String categoriaMasReciclada;
    private String semana;
}