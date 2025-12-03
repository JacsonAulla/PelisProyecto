package com.MediaWeseco.Back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MediaWeseco.Back.models.Rol;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Busca un rol por su nombre
    Optional<Rol> findByNombre(String nombre);
}