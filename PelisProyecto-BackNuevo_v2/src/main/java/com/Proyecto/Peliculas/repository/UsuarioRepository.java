package com.Proyecto.Peliculas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Proyecto.Peliculas.models.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<Usuario> findByUsernameOrEmail(String username, String email);

    Optional<Usuario> findByEmail(String email);
}

