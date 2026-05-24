package com.shopeasy.api.service;

import com.shopeasy.api.dto.*;
import com.shopeasy.api.model.User;
import com.shopeasy.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service    // Equivalente a exportar una clase de servicio en Node
public class UserService {

    @Autowired  // Inyección de dependencias (como el constructor en Node)
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registrar(RegisterRequest request) {

        // Validar duplicados
        if (userRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (userRepository.existsByCedula(request.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada");
        }

        // Crear usuario
        User user = new User();
        user.setCedula(request.getCedula());
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setCelular(request.getCelular());
        user.setCorreo(request.getCorreo());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar

        return userRepository.save(user); // INSERT INTO users ...
    }

    public User login(LoginRequest request) {

        // Buscar usuario por correo
        User user = userRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        return user;
    }
}