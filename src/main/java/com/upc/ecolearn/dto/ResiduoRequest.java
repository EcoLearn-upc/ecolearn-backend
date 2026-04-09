package com.upc.ecolearn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResiduoRequest {

    @NotBlank(message = "El usuarioId es obligatorio")
    private String usuarioId;

    @NotBlank(message = "La imagen es obligatoria")
    private String imagenUrl;
}