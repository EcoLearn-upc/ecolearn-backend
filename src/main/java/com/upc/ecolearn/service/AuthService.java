package com.upc.ecolearn.service;

import com.upc.ecolearn.dto.AuthResponse;
import com.upc.ecolearn.dto.LoginRequest;
import com.upc.ecolearn.dto.RegisterRequest;
import com.upc.ecolearn.model.Usuario;
import com.upc.ecolearn.repository.UsuarioRepository;
import com.upc.ecolearn.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.upc.ecolearn.exception.EcoLearnException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setGrado(request.getGrado());
        usuario.setSeccion(request.getSeccion());
        usuario.setColegio(request.getColegio());
        usuario.setPuntos(0);
        usuario.setNivel(1);
        usuario.setLogros(new ArrayList<>());
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getGrado(),
                usuario.getSeccion(),
                usuario.getColegio(),
                usuario.getPuntos(),
                usuario.getNivel()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getGrado(),
                usuario.getSeccion(),
                usuario.getColegio(),
                usuario.getPuntos(),
                usuario.getNivel()
        );
    }
}