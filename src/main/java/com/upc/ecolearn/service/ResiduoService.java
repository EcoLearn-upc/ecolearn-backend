package com.upc.ecolearn.service;

import com.upc.ecolearn.client.AiClassifierClient;
import com.upc.ecolearn.config.PuntosConfig;
import com.upc.ecolearn.dto.ClasificacionPreguntaResponse;
import com.upc.ecolearn.dto.ResiduoResponse;
import com.upc.ecolearn.exception.EcoLearnException;
import com.upc.ecolearn.model.Residuo;
import com.upc.ecolearn.model.SesionClasificacion;
import com.upc.ecolearn.repository.ResiduoRepository;
import com.upc.ecolearn.repository.SesionClasificacionRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ResiduoService {

    @Autowired private ResiduoRepository residuoRepository;
    @Autowired private SesionClasificacionRepository sesionClasificacionRepository;
    @Autowired private AiClassifierClient aiClassifierClient;
    @Autowired private PuntosConfig puntosConfig;
    @Autowired private GridFsTemplate gridFsTemplate;
    @Autowired private GridFsOperations gridFsOperations;
    @Autowired private UsuarioService usuarioService;
    @Autowired private LogroService logroService;
    @Autowired private RetoService retoService;
    @Autowired private MetricaAulaService metricaAulaService;

    public ResiduoResponse clasificar(String usuarioId, MultipartFile imagen) {
        AiClassifierClient.AiClasificacionResult resultado = aiClassifierClient.clasificar(imagen);

        if (!resultado.exitoso()) {
            throw new EcoLearnException("Error al clasificar la imagen", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String gridFsId = guardarImagen(imagen);

        boolean esCorrecta = puntosConfig.esClasificacionCorrecta(resultado.confianza());
        int puntos = puntosConfig.calcularPuntos(resultado.confianza());

        Residuo residuo = new Residuo();
        residuo.setUsuarioId(usuarioId);
        residuo.setGridFsId(gridFsId);
        residuo.setCategoriaDetectada(resultado.categoria());
        residuo.setConfianza(resultado.confianza());
        residuo.setEsCorrecta(esCorrecta);
        residuo.setPuntosGanados(puntos);
        residuo.setFecha(LocalDateTime.now());
        residuoRepository.save(residuo);

        usuarioService.agregarPuntos(usuarioId, puntos);
        logroService.verificarLogros(usuarioId);
        retoService.actualizarProgreso(usuarioId, resultado.categoria());
        metricaAulaService.actualizarMetrica(usuarioId);

        return new ResiduoResponse(
                residuo.getId(),
                residuo.getUsuarioId(),
                residuo.getGridFsId(),
                residuo.getCategoriaDetectada(),
                residuo.getConfianza(),
                residuo.isEsCorrecta(),
                residuo.getPuntosGanados(),
                residuo.getFecha(),
                resultado.recomendacion()
        );
    }

    // Paso 1: clasifica la imagen y devuelve 4 opciones sin revelar la correcta
    public ClasificacionPreguntaResponse clasificarConPregunta(String usuarioId, MultipartFile imagen) {
        AiClassifierClient.AiClasificacionResult resultado = aiClassifierClient.clasificar(imagen);

        if (!resultado.exitoso()) {
            throw new EcoLearnException("Error al clasificar la imagen", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String gridFsId = guardarImagen(imagen);

        SesionClasificacion sesion = new SesionClasificacion();
        sesion.setUsuarioId(usuarioId);
        sesion.setCategoriaDetectada(resultado.categoria());
        sesion.setConfianza(resultado.confianza());
        sesion.setGridFsId(gridFsId);
        sesion.setRespondida(false);
        sesion.setFecha(LocalDateTime.now());
        sesionClasificacionRepository.save(sesion);

        List<String> opciones = generarOpciones(resultado.categoria());

        return new ClasificacionPreguntaResponse(sesion.getId(), opciones);
    }

    // Paso 2: recibe la respuesta del niño y devuelve el resultado completo
    public ResiduoResponse responderPrediccion(String usuarioId, String sesionId, String respuestaUsuario) {
        SesionClasificacion sesion = sesionClasificacionRepository.findById(sesionId)
                .orElseThrow(() -> new EcoLearnException("Sesión no encontrada", HttpStatus.NOT_FOUND));

        if (sesion.isRespondida()) {
            throw new EcoLearnException("Esta clasificación ya fue respondida", HttpStatus.CONFLICT);
        }

        if (!sesion.getUsuarioId().equals(usuarioId)) {
            throw new EcoLearnException("No autorizado", HttpStatus.FORBIDDEN);
        }

        boolean adivinoCorrectamente = sesion.getCategoriaDetectada().equalsIgnoreCase(respuestaUsuario);

        boolean esCorrecta = puntosConfig.esClasificacionCorrecta(sesion.getConfianza());
        int puntosBase = puntosConfig.calcularPuntos(sesion.getConfianza());
        int puntosBonus = adivinoCorrectamente ? 10 : 0;
        int puntosTotales = puntosBase + puntosBonus;

        Residuo residuo = new Residuo();
        residuo.setUsuarioId(usuarioId);
        residuo.setGridFsId(sesion.getGridFsId());
        residuo.setCategoriaDetectada(sesion.getCategoriaDetectada());
        residuo.setConfianza(sesion.getConfianza());
        residuo.setEsCorrecta(esCorrecta);
        residuo.setPuntosGanados(puntosTotales);
        residuo.setFecha(LocalDateTime.now());
        residuoRepository.save(residuo);

        sesion.setRespondida(true);
        sesionClasificacionRepository.save(sesion);

        usuarioService.agregarPuntos(usuarioId, puntosTotales);
        logroService.verificarLogros(usuarioId);
        retoService.actualizarProgreso(usuarioId, sesion.getCategoriaDetectada());
        metricaAulaService.actualizarMetrica(usuarioId);

        return new ResiduoResponse(
                residuo.getId(),
                residuo.getUsuarioId(),
                residuo.getGridFsId(),
                residuo.getCategoriaDetectada(),
                residuo.getConfianza(),
                residuo.isEsCorrecta(),
                residuo.getPuntosGanados(),
                residuo.getFecha(),
                null
        );
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

    private List<String> generarOpciones(String categoriaCorrecta) {
        List<String> todas = new ArrayList<>(List.of(
                "battery", "biological", "cardboard", "clothes", "glass",
                "metal", "paper", "plastic", "shoes", "trash"
        ));
        todas.remove(categoriaCorrecta);
        Collections.shuffle(todas);
        List<String> opciones = new ArrayList<>(todas.subList(0, 3));
        opciones.add(categoriaCorrecta);
        Collections.shuffle(opciones);
        return opciones;
    }
}