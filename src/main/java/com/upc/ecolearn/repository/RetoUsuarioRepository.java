package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.RetoUsuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RetoUsuarioRepository extends MongoRepository<RetoUsuario, String> {

    List<RetoUsuario> findByUsuarioId(String usuarioId);

    List<RetoUsuario> findByUsuarioIdAndCompletadoTrue(String usuarioId);

    Optional<RetoUsuario> findByUsuarioIdAndRetoId(String usuarioId, String retoId);

    long countByUsuarioIdAndCompletadoTrue(String usuarioId);
}