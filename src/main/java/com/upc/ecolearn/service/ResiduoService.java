package com.upc.ecolearn.service;

import com.upc.ecolearn.client.AiClassifierClient;
import com.upc.ecolearn.config.PuntosConfig;
import com.upc.ecolearn.exception.EcoLearnException;
import com.upc.ecolearn.model.Residuo;
import com.upc.ecolearn.repository.ResiduoRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResiduoService {

    @Autowired private ResiduoRepository residuoRepository;
    @Autowired private AiClassifierClient aiClassifierClient;
    @Autowired private PuntosConfig puntosConfig;
    @Autowired private GridFsTemplate gridFsTemplate;
    @Autowired private GridFsOperations gridFsOperations;
    @Autowired private UsuarioService usuarioService;
    @Autowired private LogroService logroService;
    @Autowired private RetoService retoService;
    @Autowired private MetricaAulaService metricaAulaService;


    public Residuo clasificar(String usuarioId, MultipartFile imagen) {
        // 1. Guardar imagen en GridFS
        String gridFsId = guardarImagen(imagen);

        // 2. Llamar al AI Classifier
        AiClassifierClient.AiClasificacionResult resultado = aiClassifierClient.clasificar(imagen);

        if (!resultado.exitoso()) {
            throw new EcoLearnException("Error al clasificar la imagen", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // 3. Calcular puntos y correctitud
        boolean esCorrecta = puntosConfig.esClasificacionCorrecta(resultado.confianza());
        int puntos = puntosConfig.calcularPuntos(resultado.confianza());

        // 4. Persistir residuo
        Residuo residuo = new Residuo();
        residuo.setUsuarioId(usuarioId);
        residuo.setGridFsId(gridFsId);
        residuo.setCategoriaDetectada(resultado.categoria());
        residuo.setConfianza(resultado.confianza());
        residuo.setEsCorrecta(esCorrecta);
        residuo.setPuntosGanados(puntos);
        residuo.setFecha(LocalDateTime.now());
        residuoRepository.save(residuo);

        // 5. Actualizar puntos del usuario y verificar logros
        usuarioService.agregarPuntos(usuarioId, puntos);
        logroService.verificarLogros(usuarioId);
        retoService.actualizarProgreso(usuarioId, resultado.categoria());
        metricaAulaService.actualizarMetrica(usuarioId);

        return residuo;
    }

    public InputStream obtenerImagen(String gridFsId) {
        try {
            GridFSFile file = gridFsTemplate.findOne(
                    new Query(Criteria.where("_id").is(new ObjectId(gridFsId))));
            if (file == null) throw new EcoLearnException("Imagen no encontrada", HttpStatus.NOT_FOUND);
            return gridFsOperations.getResource(file).getInputStream();
        } catch (IOException e) {
            throw new EcoLearnException("Error al recuperar imagen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Residuo> historialUsuario(String usuarioId) {
        return residuoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    private String guardarImagen(MultipartFile imagen) {
        try {
            ObjectId id = gridFsTemplate.store(
                    imagen.getInputStream(),
                    imagen.getOriginalFilename(),
                    imagen.getContentType());
            return id.toString();
        } catch (IOException e) {
            throw new EcoLearnException("Error al guardar imagen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
