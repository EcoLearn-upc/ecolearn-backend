package com.upc.ecolearn.repository;

import com.upc.ecolearn.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    java.util.List<Usuario> findByColegioAndGradoAndSeccion(
            String colegio,
            String grado,
            String seccion
    );
    List<Usuario> findByRolOrderByPuntosDesc(String rol);
    List<Usuario> findByColegio(String colegio);
}