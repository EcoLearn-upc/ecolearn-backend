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
@Document(collection = "retos")
public class Reto {

    @Id
    private String id;
    private String titulo;
    private String descripcion;
    private String tipo;// semanal, trivia
    private int puntosRecompensa;
    private int meta;
    private String categoriaResiduo;// plastico, papel, vidrio, metal, organico, null si es trivia
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean activo;
}
