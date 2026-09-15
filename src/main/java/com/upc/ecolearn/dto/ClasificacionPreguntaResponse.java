package com.upc.ecolearn.dto;

import java.util.List;

public record ClasificacionPreguntaResponse(
        String sesionId,
        List<String> opciones
) {}