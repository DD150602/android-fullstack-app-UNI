package com.shopeasy.api.repository;

import com.shopeasy.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Equivalente a tu modelo con Sequelize/Prisma
// JpaRepository ya incluye: findAll, findById, save, delete, etc.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCorreo(String correo);     // SELECT * FROM users WHERE correo = ?
    Optional<User> findByCedula(String cedula);
    boolean existsByCorreo(String correo);
    boolean existsByCedula(String cedula);
}