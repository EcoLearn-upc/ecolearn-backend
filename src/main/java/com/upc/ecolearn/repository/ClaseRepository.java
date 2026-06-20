package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.Clase;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaseRepository extends MongoRepository<Clase, String> {

    Optional<Clase> findByCodigoAcceso(String codigoAcceso);

    List<Clase> findByDocenteId(String docenteId);

    boolean existsByCodigoAcceso(String codigoAcceso);
}