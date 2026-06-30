package com.upc.ecolearn.client;

import org.springframework.web.multipart.MultipartFile;

public interface AiClassifierClient {

    AiClasificacionResult clasificar(MultipartFile imagen);

    record AiClasificacionResult(
            String categoria,
            double confianza,
            boolean exitoso,
            String recomendacion
    ) {}
}