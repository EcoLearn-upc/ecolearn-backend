package com.upc.ecolearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "usuarios")
public class Usuario {

    @Id
    private String id;
    private String nombre;
    @Indexed(unique = true)

    private String correo;
    private String password;
    private String rol;//estudiante, docente, admin
    private String grado;
    private String seccion;
    private String colegio;
    private int puntos;
    private int nivel;
    private List<String> logros = new ArrayList<>();
    private LocalDateTime fechaRegistro;
    private boolean activo;

}
