package com.upc.ecolearn.client;

import com.upc.ecolearn.exception.EcoLearnException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "ai.service.enabled", havingValue = "true")
public class RealAiClassifierClient implements AiClassifierClient {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public AiClasificacionResult clasificar(MultipartFile imagen) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(imagen.getBytes()) {
                @Override
                public String getFilename() { return imagen.getOriginalFilename(); }
            });

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServiceUrl + "/predict", request, Map.class);

            Map<?, ?> result = response.getBody();
            return new AiClasificacionResult(
                    (String) result.get("clase"),
                    ((Number) result.get("confianza")).doubleValue(),
                    true,
                    (String) result.get("recomendacion")
            );
        } catch (IOException e) {
            throw new EcoLearnException("Error al procesar la imagen", HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            throw new EcoLearnException("AI Service no disponible", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}