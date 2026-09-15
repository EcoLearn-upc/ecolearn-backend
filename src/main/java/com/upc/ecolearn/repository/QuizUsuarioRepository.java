package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.QuizUsuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizUsuarioRepository extends MongoRepository<QuizUsuario, String> {
    List<QuizUsuario> findByUsuarioIdOrderByFechaDesc(String usuarioId);
}