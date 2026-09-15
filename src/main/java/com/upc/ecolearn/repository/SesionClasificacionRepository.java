package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.SesionClasificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SesionClasificacionRepository extends MongoRepository<SesionClasificacion, String> {
}