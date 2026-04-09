package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.Logro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogroRepository extends MongoRepository<Logro, String> {

    List<Logro> findByTipo(String tipo);
}