package com.shopeasy.api.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String cedula;
    private String nombre;
    private String apellido;
    private String celular;
    private String correo;
    private String password;
}