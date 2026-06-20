package com.upc.ecolearn.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clases")
public class Clase {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigoAcceso; // ej: ECO-CPL-5A

    private String nombre;
    private String docenteId;
    private String grado;
    private String seccion;
    private String colegio;

    private List<String> alumnosIds = new ArrayList<>(); // referencia a Usuario._id

    private LocalDateTime fechaCreacion;
    private boolean activa;
}