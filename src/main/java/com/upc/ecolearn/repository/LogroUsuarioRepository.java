package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.LogroUsuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogroUsuarioRepository extends MongoRepository<LogroUsuario, String> {

    List<LogroUsuario> findByUsuarioId(String usuarioId);

    Optional<LogroUsuario> findByUsuarioIdAndLogroId(String usuarioId, String logroId);

    boolean existsByUsuarioIdAndLogroId(String usuarioId, String logroId);
}