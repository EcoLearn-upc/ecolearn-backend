package com.upc.ecolearn.service;

import com.upc.ecolearn.config.PuntosConfig;
import com.upc.ecolearn.model.Logro;
import com.upc.ecolearn.model.LogroUsuario;
import com.upc.ecolearn.repository.LogroRepository;
import com.upc.ecolearn.repository.LogroUsuarioRepository;
import com.upc.ecolearn.repository.ResiduoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogroService {

    @Autowired private LogroRepository logroRepository;
    @Autowired private LogroUsuarioRepository logroUsuarioRepository;
    @Autowired private ResiduoRepository residuoRepository;
    @Autowired private UsuarioService usuarioService;
    @Autowired private PuntosConfig puntosConfig;

    public void verificarLogros(String usuarioId) {
        List<Logro> todos = logroRepository.findAll();
        for (Logro logro : todos) {
            if (logroUsuarioRepository.existsByUsuarioIdAndLogroId(usuarioId, logro.getId())) {
                continue; // ya obtenido
            }
            if (cumpleCondicion(usuarioId, logro)) {
                otorgarLogro(usuarioId, logro);
            }
        }
    }

    public List<LogroUsuario> obtenerLogrosUsuario(String usuarioId) {
        return logroUsuarioRepository.findByUsuarioId(usuarioId);
    }

    public List<Logro> obtenerTodos() {
        return logroRepository.findAll();
    }

    // Solo ADMIN
    public Logro crear(Logro logro) {
        return logroRepository.save(logro);
    }

    private boolean cumpleCondicion(String usuarioId, Logro logro) {
        if (logro.getCondicion() == null) return false;

        String tipo = logro.getTipo();
        Object valorObj = logro.getCondicion().get("cantidad");
        if (valorObj == null) return false;
        int cantidad = ((Number) valorObj).intValue();

        return switch (tipo) {
            case "clasificacion" -> residuoRepository.countByUsuarioId(usuarioId) >= cantidad;
            case "clasificacion_correcta" ->
                    residuoRepository.countByUsuarioIdAndEsCorrecta(usuarioId, true) >= cantidad;
            case "nivel" -> usuarioService.findById(usuarioId).getNivel() >= cantidad;
            default -> false;
        };
    }

    private void otorgarLogro(String usuarioId, Logro logro) {
        LogroUsuario lu = new LogroUsuario();
        lu.setUsuarioId(usuarioId);
        lu.setLogroId(logro.getId());
        lu.setFechaObtenido(LocalDateTime.now());
        logroUsuarioRepository.save(lu);

        // bonus de puntos por logro
        usuarioService.agregarPuntos(usuarioId, puntosConfig.getPuntosLogroBonus());
    }
}