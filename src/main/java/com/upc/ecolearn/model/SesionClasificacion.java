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
@Document(collection = "sesiones_clasificacion")
public class SesionClasificacion {

    @Id
    private String id;
    private String usuarioId;
    private String categoriaDetectada;
    private double confianza;
    private String gridFsId;
    private boolean respondida;
    private LocalDateTime fecha;
}