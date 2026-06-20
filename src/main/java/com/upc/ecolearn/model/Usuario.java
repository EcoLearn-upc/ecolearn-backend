package com.upc.ecolearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String nombre;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String rol; // ESTUDIANTE, DOCENTE, ADMIN

    private String grado;
    private String seccion;
    private String colegio;

    private String pin; // solo aplica si rol == ESTUDIANTE, null para DOCENTE/ADMIN

    // grado + seccion componen el aula lógica (ej: "5A")
    // no se persiste, se deriva cuando se necesita
    public String getAula() {
        return (grado != null && seccion != null) ? grado + seccion : null;
    }

    private int puntos;
    private int nivel;

    @CreatedDate
    private LocalDateTime fechaRegistro;

    private boolean activo;
}