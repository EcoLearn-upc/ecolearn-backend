package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.Residuo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResiduoRepository extends MongoRepository<Residuo, String> {

    List<Residuo> findByUsuarioId(String usuarioId);

    List<Residuo> findByUsuarioIdAndCategoriaDetectada(
            String usuarioId,
            String categoriaDetectada
    );

    long countByUsuarioId(String usuarioId);
    List<Residuo> findByUsuarioIdOrderByFechaDesc(String usuarioId);
    long countByUsuarioIdAndEsCorrecta(String usuarioId, boolean esCorrecta);

    long countByUsuarioIdAndCategoriaDetectada(
            String usuarioId,
            String categoriaDetectada
    );
}