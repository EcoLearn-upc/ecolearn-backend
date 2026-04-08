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
@Document(collection = "retos_usuario")
public class RetoUsuario {
    @Id
    private String id;
    private String usuarioId;
    private String retoId;
    private int progreso;
    private boolean completado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCompletado;
}
