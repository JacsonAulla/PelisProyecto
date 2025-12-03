package com.MediaWeseco.Back.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MediaWeseco.Back.models.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Busca un usuario por su email
    Optional<Usuario> findByEmail(String email);

    // Busca un usuario por su username
    Optional<Usuario> findByUsername(String username);

    // Verifica si existe un usuario con el email
    boolean existsByEmail(String email);

    // Verifica si existe un usuario con el username
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = { "roles" })
    Optional<Usuario> findByVerificationCode(String verificationCode);

}