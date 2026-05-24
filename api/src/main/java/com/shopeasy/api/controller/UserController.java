package com.shopeasy.api.controller;

import com.shopeasy.api.model.User;
import com.shopeasy.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET /api/user/profile
    @GetMapping("/profile")
    public ResponseEntity<?> profile() {

        // Obtener el correo del token (lo puso el JwtFilter)
        String correo = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(Map.of(
                "nombre",   user.getNombre(),
                "apellido", user.getApellido(),
                "correo",   user.getCorreo(),
                "cedula",   user.getCedula(),
                "celular",  user.getCelular()
        ));
    }

    // POST /api/auth/logout (el logout real lo maneja el Android borrando el token)
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente"));
    }
}