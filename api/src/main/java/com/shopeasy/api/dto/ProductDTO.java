package com.shopeasy.api.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
}