package com.upc.ecolearn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String grado;
    private String seccion;
    private String colegio;
    private int puntos;
    private int nivel;
    private List<String> logros;
    private LocalDateTime fechaRegistro;
    private boolean activo;
}