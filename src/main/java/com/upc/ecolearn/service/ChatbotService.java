package com.upc.ecolearn.service;

import com.upc.ecolearn.exception.EcoLearnException;
import com.upc.ecolearn.model.ChatbotHistorial;
import com.upc.ecolearn.repository.ChatbotHistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    @Autowired private ChatbotHistorialRepository chatbotHistorialRepository;

    private static final String SYSTEM_PROMPT = """
            Eres EcoBot, un asistente educativo amigable para niños de primaria.
            Tu especialidad es enseñar sobre reciclaje y clasificación de residuos.
            Responde siempre en español, con lenguaje simple y motivador.
            Categorías de residuos que manejas: plástico, papel, vidrio, metal, cartón, orgánico, baterías, zapatos, ropa, basura general.
            Nunca respondas temas fuera del reciclaje y medio ambiente.
            Máximo 3 oraciones por respuesta.
            """;

    public ChatbotHistorial enviarMensaje(String usuarioId, String mensaje) {
        ChatbotHistorial historial = chatbotHistorialRepository
                .findTopByUsuarioIdOrderByFechaInicioDesc(usuarioId)
                .orElseGet(() -> crearNuevaSesion(usuarioId));

        historial.getMensajes().add(new ChatbotHistorial.Mensaje("usuario", mensaje, LocalDateTime.now()));

        List<Map<String, String>> mensajesGroq = new ArrayList<>();
        mensajesGroq.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        historial.getMensajes().forEach(m -> mensajesGroq.add(
                Map.of("role", m.getRol().equals("usuario") ? "user" : "assistant",
                        "content", m.getContenido())));

        String respuesta = llamarGroq(mensajesGroq);

        historial.getMensajes().add(new ChatbotHistorial.Mensaje("chatbot", respuesta, LocalDateTime.now()));
        return chatbotHistorialRepository.save(historial);
    }

    public List<ChatbotHistorial> obtenerHistorial(String usuarioId) {
        return chatbotHistorialRepository.findByUsuarioId(usuarioId);
    }

    public ChatbotHistorial nuevaSesion(String usuarioId) {
        return chatbotHistorialRepository.save(crearNuevaSesion(usuarioId));
    }

    private String llamarGroq(List<Map<String, String>> mensajes) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + groqApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "model", groqModel,
                    "messages", mensajes,
                    "max_tokens", 200,
                    "temperature", 0.7
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(groqApiUrl, request, Map.class);

            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            throw new EcoLearnException("EcoBot no disponible temporalmente", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private ChatbotHistorial crearNuevaSesion(String usuarioId) {
        ChatbotHistorial h = new ChatbotHistorial();
        h.setUsuarioId(usuarioId);
        h.setMensajes(new ArrayList<>());
        h.setFechaInicio(LocalDateTime.now());
        return h;
    }
}