package com.upc.ecolearn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String grado;
    private String seccion;
    private String colegio;
    private int puntos;
    private int nivel;
}