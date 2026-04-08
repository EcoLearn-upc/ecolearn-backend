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
@Document(collection = "residuos")

public class Residuo {
    @Id
    private String id;
    private String usuarioId;
    private String imageUrl;
    private String categoriaDetectada;//plástico, papel, vidrio, orgánico
    private double confianza;
    private boolean esCorrecta;
    private int puntosGanados;
    private LocalDateTime fecha;
}
