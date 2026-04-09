package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.Reto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetoRepository extends MongoRepository<Reto, String> {

    List<Reto> findByActivoTrue();

    List<Reto> findByTipo(String tipo);

    List<Reto> findByActivoTrueAndTipo(String tipo);
}