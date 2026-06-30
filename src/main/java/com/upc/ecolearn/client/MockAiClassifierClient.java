package com.upc.ecolearn.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "ai.service.enabled", havingValue = "false", matchIfMissing = true)
public class MockAiClassifierClient implements AiClassifierClient {

    private static final List<String> CATEGORIAS = List.of(
            "plastic", "paper", "glass", "metal",
            "cardboard", "biological", "battery", "trash", "shoes", "clothes"
    );

    @Override
    public AiClasificacionResult clasificar(MultipartFile imagen) {
        Random random = new Random();
        String categoria = CATEGORIAS.get(random.nextInt(CATEGORIAS.size()));
        double confianza = 0.70 + (random.nextDouble() * 0.29); // 0.70 - 0.99
        return new AiClasificacionResult(categoria, confianza, true, null);
    }
}