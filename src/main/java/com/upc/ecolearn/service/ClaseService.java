package com.upc.ecolearn.service;

import com.upc.ecolearn.exception.EcoLearnException;
import com.upc.ecolearn.model.Clase;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.repository.ClaseRepository;
import com.upc.ecolearn.repository.UsuarioRepository;
import com.upc.ecolearn.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.Map;

@Service
public class ClaseService {

    @Autowired private ClaseRepository claseRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    // Docente crea la clase — el código viene generado desde el frontend
    public Clase crearClase(String docenteId, String nombre, String grado, String seccion, String colegio, String codigoAcceso) {
        if (codigoAcceso == null || codigoAcceso.isBlank()) {
            throw new EcoLearnException("El código de acceso es obligatorio", HttpStatus.BAD_REQUEST);
        }
        if (claseRepository.existsByCodigoAcceso(codigoAcceso)) {
            throw new EcoLearnException("Ya existe una clase con ese código", HttpStatus.CONFLICT);
        }

        Clase clase = new Clase();
        clase.setNombre(nombre);
        clase.setDocenteId(docenteId);
        clase.setGrado(grado);
        clase.setSeccion(seccion);
        clase.setColegio(colegio);
        clase.setCodigoAcceso(codigoAcceso.toUpperCase());
        clase.setAlumnosIds(new ArrayList<>());
        clase.setFechaCreacion(LocalDateTime.now());
        clase.setActiva(true);
        return claseRepository.save(clase);
    }
    public Map<String, Object> obtenerInfoPublica(String codigoAcceso) {
        Clase clase = claseRepository.findByCodigoAcceso(codigoAcceso)
                .orElseThrow(() -> new EcoLearnException("Código de clase inválido", HttpStatus.NOT_FOUND));

        List<String> nombres = clase.getAlumnosIds().stream()
                .map(usuarioRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(Usuario::getNombre)
                .toList();

        return Map.of(
                "claseId", clase.getId(),
                "nombre", clase.getNombre(),
                "codigoAcceso", clase.getCodigoAcceso(),
                "alumnos", nombres
        );
    }
    public Map<String, Object> obtenerDetallePorCodigo(String codigoAcceso) {
        Clase clase = claseRepository.findByCodigoAcceso(codigoAcceso)
                .orElseThrow(() -> new EcoLearnException("Código de clase inválido", HttpStatus.NOT_FOUND));

        List<Usuario> alumnos = clase.getAlumnosIds().stream()
                .map(usuarioRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .toList();

        return Map.of(
                "claseId", clase.getId(),
                "nombre", clase.getNombre(),
                "colegio", clase.getColegio(),
                "codigoAcceso", clase.getCodigoAcceso(),
                "alumnos", alumnos
        );
    }
    // Docente agrega alumnos por nombre — genera Usuario + PIN para cada uno
    public List<Usuario> agregarAlumnos(String claseId, List<String> nombres) {
        Clase clase = findById(claseId);
        List<Usuario> creados = new ArrayList<>();

        for (String nombre : nombres) {
            String pin = generarPin();
            String emailFicticio = generarEmailEstudiante(nombre, clase.getCodigoAcceso());

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(emailFicticio);
            usuario.setPassword(passwordEncoder.encode(pin));
            usuario.setPin(pin); // se guarda en claro solo para mostrar al docente una vez
            usuario.setRol("ESTUDIANTE");
            usuario.setGrado(clase.getGrado());
            usuario.setSeccion(clase.getSeccion());
            usuario.setColegio(clase.getColegio());
            usuario.setPuntos(0);
            usuario.setNivel(1);
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario.setActivo(true);

            usuarioRepository.save(usuario);
            clase.getAlumnosIds().add(usuario.getId());
            creados.add(usuario);
        }

        claseRepository.save(clase);
        return creados;
    }

    // Estudiante entra con código de clase + nombre + PIN
    public String loginEstudiante(String codigoAcceso, String nombre, String pin) {
        Clase clase = claseRepository.findByCodigoAcceso(codigoAcceso)
                .orElseThrow(() -> new EcoLearnException("Código de clase inválido", HttpStatus.NOT_FOUND));

        Usuario usuario = clase.getAlumnosIds().stream()
                .map(usuarioRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .filter(u -> u.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElseThrow(() -> new EcoLearnException("Alumno no encontrado en esta clase", HttpStatus.NOT_FOUND));

        if (!pin.equals(usuario.getPin())) {
            throw new EcoLearnException("PIN incorrecto", HttpStatus.UNAUTHORIZED);
        }

        return jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());
    }

    public List<Clase> obtenerClasesDocente(String docenteId) {
        return claseRepository.findByDocenteId(docenteId);
    }

    public List<Usuario> obtenerAlumnos(String claseId) {
        Clase clase = findById(claseId);
        return clase.getAlumnosIds().stream()
                .map(usuarioRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .toList();
    }

    public Clase findById(String claseId) {
        return claseRepository.findById(claseId)
                .orElseThrow(() -> new EcoLearnException("Clase no encontrada", HttpStatus.NOT_FOUND));
    }

    private String generarPin() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    private String generarEmailEstudiante(String nombre, String codigoClase) {
        String slug = nombre.trim().toLowerCase().replaceAll("\\s+", ".");
        return slug + "." + codigoClase.toLowerCase() + "@ecolearn.local";
    }
}