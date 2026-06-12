package com.upc.ecolearn.service;

import com.upc.ecolearn.config.PuntosConfig;
import com.upc.ecolearn.dto.UsuarioPerfilResponse;
import com.upc.ecolearn.exception.EcoLearnException;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.repository.LogroUsuarioRepository;
import com.upc.ecolearn.repository.ResiduoRepository;
import com.upc.ecolearn.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PuntosConfig puntosConfig;
    @Autowired private ResiduoRepository residuoRepository;
    @Autowired private LogroUsuarioRepository logroUsuarioRepository;

    public void agregarPuntos(String usuarioId, int puntos) {
        Usuario usuario = findById(usuarioId);
        usuario.setPuntos(usuario.getPuntos() + puntos);
        usuario.setNivel(puntosConfig.calcularNivel(usuario.getPuntos()));
        usuarioRepository.save(usuario);
    }

    public UsuarioPerfilResponse obtenerPerfil(String usuarioId) {
        Usuario usuario = findById(usuarioId);
        long totalClasificaciones = residuoRepository.countByUsuarioId(usuarioId);
        long correctas = residuoRepository.countByUsuarioIdAndEsCorrecta(usuarioId, true);
        int totalLogros = logroUsuarioRepository.findByUsuarioId(usuarioId).size();

        return new UsuarioPerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getGrado(),
                usuario.getSeccion(),
                usuario.getColegio(),
                usuario.getPuntos(),
                usuario.getNivel(),
                (int) totalClasificaciones,
                (int) correctas,
                totalLogros
        );
    }

    public List<Usuario> obtenerRanking() {
        return usuarioRepository.findByRolOrderByPuntosDesc("ESTUDIANTE");
    }

    public Usuario findById(String usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EcoLearnException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EcoLearnException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }
}