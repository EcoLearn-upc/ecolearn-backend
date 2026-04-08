package com.upc.ecolearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "logros")
public class Logro {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String icono;
    private String tipo;// clasificacion, retos, racha
    private Map<String, Object> condicion;
    private int puntosBonus;
}