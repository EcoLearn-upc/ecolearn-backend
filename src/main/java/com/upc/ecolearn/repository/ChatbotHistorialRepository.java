package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.ChatbotHistorial;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatbotHistorialRepository extends MongoRepository<ChatbotHistorial, String> {

    List<ChatbotHistorial> findByUsuarioId(String usuarioId);

    Optional<ChatbotHistorial> findTopByUsuarioIdOrderByFechaInicioDesc(String usuarioId);
}