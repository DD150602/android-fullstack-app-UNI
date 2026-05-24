package com.shopeasy.api.controller;

import com.shopeasy.api.dto.*;
import com.shopeasy.api.model.User;
import com.shopeasy.api.security.JwtUtil;
import com.shopeasy.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController                         // Equivalente a express.Router()
@RequestMapping("/api/auth")            // Prefijo de todas las rutas
@CrossOrigin(origins = "*")            // Equivalente a cors()
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Validaciones básicas
            if (request.getCedula() == null || request.getCedula().isBlank() ||
                    request.getNombre() == null || request.getNombre().isBlank() ||
                    request.getCorreo() == null || request.getCorreo().isBlank() ||
                    request.getPassword() == null || request.getPassword().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Todos los campos son obligatorios"));
            }

            // Validar formato de correo
            if (!request.getCorreo().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Correo inválido"));
            }

            User user = userService.registrar(request);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Usuario registrado exitosamente",
                    "correo", user.getCorreo()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            String token = jwtUtil.generarToken(user.getCorreo());

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    user.getNombre(),
                    user.getCorreo(),
                    "Login exitoso"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}