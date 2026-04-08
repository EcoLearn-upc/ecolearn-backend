package com.upc.ecolearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "metricas_aula")
public class MetricaAula {

    @Id
    private String id;
    private String colegio;
    private String grado;
    private String seccion;
    private int totalClasificaciones;
    private int totalPuntos;
    private int retosCompletados;
    private String categoriaMasReciclada;
    private String semana;
    private LocalDateTime fechaActualizacion;
}