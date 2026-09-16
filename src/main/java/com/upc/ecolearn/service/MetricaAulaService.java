package com.upc.ecolearn.service;

import com.upc.ecolearn.model.MetricaAula;
import com.upc.ecolearn.model.Residuo;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.repository.MetricaAulaRepository;
import com.upc.ecolearn.repository.ResiduoRepository;
import com.upc.ecolearn.repository.RetoUsuarioRepository;
import com.upc.ecolearn.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetricaAulaService {

    @Autowired private MetricaAulaRepository metricaAulaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ResiduoRepository residuoRepository;
    @Autowired private RetoUsuarioRepository retoUsuarioRepository;

    public void actualizarMetrica(String usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null || usuario.getAula() == null) return;

        String semana = getSemanaActual();
        MetricaAula metrica = metricaAulaRepository
                .findByColegioAndGradoAndSeccionAndSemana(
                        usuario.getColegio(), usuario.getGrado(), usuario.getSeccion(), semana)
                .orElseGet(() -> crearNuevaMetrica(usuario, semana));

        List<Usuario> companeros = usuarioRepository.findByColegioAndGradoAndSeccion(
                usuario.getColegio(), usuario.getGrado(), usuario.getSeccion());

        int totalClasificaciones = 0;
        int totalPuntos = 0;
        long totalRetos = 0;
        int alumnosActivos = 0;
        Map<String, Long> categorias = new HashMap<>();

        for (Usuario u : companeros) {
            List<Residuo> residuos = residuoRepository.findByUsuarioId(u.getId());
            totalClasificaciones += residuos.size();
            totalPuntos += u.getPuntos();
            totalRetos += retoUsuarioRepository.countByUsuarioIdAndCompletadoTrue(u.getId());
            residuos.forEach(r -> categorias.merge(r.getCategoriaDetectada(), 1L, Long::sum));

            boolean activoEstaSemana = u.getPuntos() > 0;
            if (activoEstaSemana) alumnosActivos++;
        }

        int total = companeros.size();
        int alumnosInactivos = total - alumnosActivos;
        int porcentajeParticipacion = total > 0 ? Math.round((alumnosActivos * 100f) / total) : 0;
        int progresoGeneral = total > 0 ? Math.round((totalPuntos * 100f) / (total * 100)) : 0;
        progresoGeneral = Math.min(progresoGeneral, 100);

        String categoriaMas = categorias.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        metrica.setTotalClasificaciones(totalClasificaciones);
        metrica.setTotalPuntos(totalPuntos);
        metrica.setRetosCompletados((int) totalRetos);
        metrica.setCategoriaMasReciclada(categoriaMas);
        metrica.setAlumnosActivos(alumnosActivos);
        metrica.setAlumnosInactivos(alumnosInactivos);
        metrica.setPorcentajeParticipacion(porcentajeParticipacion);
        metrica.setProgresoGeneral(progresoGeneral);
        metrica.setFechaActualizacion(LocalDateTime.now());
        metricaAulaRepository.save(metrica);
    }

    public MetricaAula obtenerMetricaActual(String colegio, String grado, String seccion) {
        String semana = getSemanaActual();
        return metricaAulaRepository
                .findByColegioAndGradoAndSeccionAndSemana(colegio, grado, seccion, semana)
                .orElse(null);
    }

    public List<MetricaAula> obtenerPorColegio(String colegio) {
        return metricaAulaRepository.findByColegio(colegio);
    }

    public List<MetricaAula> obtenerPorAula(String colegio, String grado, String seccion) {
        return metricaAulaRepository.findByColegioAndGradoAndSeccion(colegio, grado, seccion);
    }

    private MetricaAula crearNuevaMetrica(Usuario usuario, String semana) {
        MetricaAula m = new MetricaAula();
        m.setColegio(usuario.getColegio());
        m.setGrado(usuario.getGrado());
        m.setSeccion(usuario.getSeccion());
        m.setSemana(semana);
        return m;
    }

    private String getSemanaActual() {
        LocalDateTime now = LocalDateTime.now();
        int semana = now.get(WeekFields.ISO.weekOfWeekBasedYear());
        return now.getYear() + "-W" + String.format("%02d", semana);
    }
}