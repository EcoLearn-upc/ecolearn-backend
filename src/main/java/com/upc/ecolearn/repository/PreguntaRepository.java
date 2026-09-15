package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.Pregunta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepository extends MongoRepository<Pregunta, String> {
    List<Pregunta> findByCategoria(String categoria);
}