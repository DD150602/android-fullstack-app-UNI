package com.shopeasy.api.service;

import com.shopeasy.api.dto.ProductDTO;
import com.shopeasy.api.model.Product;
import com.shopeasy.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // GET ALL
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // GET BY ID
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // CREATE
    public Product create(ProductDTO dto) {
        Product product = new Product();
        product.setNombre(dto.getNombre());
        product.setDescripcion(dto.getDescripcion());
        product.setPrecio(dto.getPrecio());
        product.setStock(dto.getStock());
        return productRepository.save(product);
    }

    // UPDATE
    public Product update(Long id, ProductDTO dto) {
        Product product = getById(id);
        product.setNombre(dto.getNombre());
        product.setDescripcion(dto.getDescripcion());
        product.setPrecio(dto.getPrecio());
        product.setStock(dto.getStock());
        return productRepository.save(product);
    }

    // DELETE
    public void delete(Long id) {
        getById(id); // Verifica que existe antes de borrar
        productRepository.deleteById(id);
    }
}