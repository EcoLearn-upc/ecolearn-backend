package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.MetricaAula;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetricaAulaRepository extends MongoRepository<MetricaAula, String> {

    Optional<MetricaAula> findByColegioAndGradoAndSeccionAndSemana(
            String colegio,
            String grado,
            String seccion,
            String semana
    );

    List<MetricaAula> findByColegio(String colegio);

    List<MetricaAula> findByColegioAndGradoAndSeccion(
            String colegio,
            String grado,
            String seccion
    );
}