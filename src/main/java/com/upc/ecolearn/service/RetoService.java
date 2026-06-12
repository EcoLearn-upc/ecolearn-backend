package com.upc.ecolearn.service;

import com.upc.ecolearn.exception.EcoLearnException;
import com.upc.ecolearn.model.Reto;
import com.upc.ecolearn.model.RetoUsuario;
import com.upc.ecolearn.repository.RetoRepository;
import com.upc.ecolearn.repository.RetoUsuarioRepository;
import com.upc.ecolearn.repository.ResiduoRepository;
import com.upc.ecolearn.config.PuntosConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RetoService {

    @Autowired private RetoRepository retoRepository;
    @Autowired private RetoUsuarioRepository retoUsuarioRepository;
    @Autowired private ResiduoRepository residuoRepository;
    @Autowired private UsuarioService usuarioService;
    @Autowired private PuntosConfig puntosConfig;

    // ADMIN
    public Reto crear(Reto reto) {
        reto.setActivo(true);
        return retoRepository.save(reto);
    }

    public Reto actualizar(String retoId, Reto datos) {
        Reto reto = findById(retoId);
        reto.setTitulo(datos.getTitulo());
        reto.setDescripcion(datos.getDescripcion());
        reto.setMeta(datos.getMeta());
        reto.setPuntosRecompensa(datos.getPuntosRecompensa());
        reto.setCategoriaResiduo(datos.getCategoriaResiduo());
        reto.setFechaInicio(datos.getFechaInicio());
        reto.setFechaFin(datos.getFechaFin());
        reto.setActivo(datos.isActivo());
        return retoRepository.save(reto);
    }

    public void eliminar(String retoId) {
        retoRepository.deleteById(retoId);
    }

    // USUARIO — inscribirse a un reto
    public RetoUsuario inscribirse(String usuarioId, String retoId) {
        findById(retoId); // valida existencia

        if (retoUsuarioRepository.findByUsuarioIdAndRetoId(usuarioId, retoId).isPresent()) {
            throw new EcoLearnException("Ya estás inscrito en este reto", HttpStatus.CONFLICT);
        }

        RetoUsuario ru = new RetoUsuario();
        ru.setUsuarioId(usuarioId);
        ru.setRetoId(retoId);
        ru.setProgreso(0);
        ru.setCompletado(false);
        ru.setFechaInicio(LocalDateTime.now());
        return retoUsuarioRepository.save(ru);
    }

    // Llamado desde ResiduoService al clasificar — actualiza progreso
    public void actualizarProgreso(String usuarioId, String categoriaDetectada) {
        List<Reto> retosActivos = retoRepository.findByActivoTrue();

        for (Reto reto : retosActivos) {
            // solo retos del tipo clasificacion con categoria coincidente o sin filtro
            if (reto.getCategoriaResiduo() != null &&
                    !reto.getCategoriaResiduo().equalsIgnoreCase(categoriaDetectada)) continue;

            retoUsuarioRepository.findByUsuarioIdAndRetoId(usuarioId, reto.getId())
                    .ifPresent(ru -> {
                        if (ru.isCompletado()) return;
                        ru.setProgreso(ru.getProgreso() + 1);
                        if (ru.getProgreso() >= reto.getMeta()) {
                            ru.setCompletado(true);
                            ru.setFechaCompletado(LocalDateTime.now());
                            usuarioService.agregarPuntos(usuarioId, reto.getPuntosRecompensa());
                        }
                        retoUsuarioRepository.save(ru);
                    });
        }
    }

    public List<Reto> obtenerActivos() {
        return retoRepository.findByActivoTrue();
    }

    public List<RetoUsuario> obtenerRetosUsuario(String usuarioId) {
        return retoUsuarioRepository.findByUsuarioId(usuarioId);
    }

    public Reto findById(String retoId) {
        return retoRepository.findById(retoId)
                .orElseThrow(() -> new EcoLearnException("Reto no encontrado", HttpStatus.NOT_FOUND));
    }
}