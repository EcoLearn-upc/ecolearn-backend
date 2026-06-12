package com.upc.ecolearn.dto;

public record UsuarioPerfilResponse(
        String id,
        String nombre,
        String email,
        String rol,
        String grado,
        String seccion,
        String colegio,
        int puntos,
        int nivel,
        int totalClasificaciones,
        int clasificacionesCorrectas,
        int totalLogros
) {}