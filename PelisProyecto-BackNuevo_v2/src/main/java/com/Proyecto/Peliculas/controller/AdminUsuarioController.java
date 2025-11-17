package com.Proyecto.Peliculas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // <-- AÑADIDO
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// --- IMPORTACIÓN CORREGIDA ---
// Antes: import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.RequestBody; // <-- ESTA ES LA CORRECTA
import jakarta.validation.Valid; // <-- AÑADIDO
import java.util.List; // <-- AÑADIDO

import com.Proyecto.Peliculas.dtos.Usuario.AdminUsuarioCreateDTO; // <-- AÑADIDO
import com.Proyecto.Peliculas.dtos.Usuario.AdminUsuarioUpdateDTO;
import com.Proyecto.Peliculas.dtos.Usuario.UsuarioRegistroRespuestaDTO;
import com.Proyecto.Peliculas.services.UsuarioService;


@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // --- AÑADIDO: GET / (Obtener Todos) ---
    @GetMapping
    public ResponseEntity<List<UsuarioRegistroRespuestaDTO>> obtenerTodos() {
        return new ResponseEntity<>(usuarioService.obtenerTodosLosUsuarios(), HttpStatus.OK);
    }

    // --- AÑADIDO: POST / (Crear) ---
    @PostMapping
    public ResponseEntity<UsuarioRegistroRespuestaDTO> crearUsuario(
            @Valid @RequestBody AdminUsuarioCreateDTO dto) {
        
        UsuarioRegistroRespuestaDTO nuevoUsuario = usuarioService.crearUsuarioAdmin(dto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }
    
    // --- TU MÉTODO (Corregido) ---
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRegistroRespuestaDTO> obtenerPorId(@PathVariable Long id) {
        return new ResponseEntity<>(usuarioService.obtenerUsuarioPorId(id), HttpStatus.OK);
    }

    // --- TU MÉTODO (Corregido) ---
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRegistroRespuestaDTO> actualizarUsuario(
            @PathVariable Long id, 
            @Valid @RequestBody AdminUsuarioUpdateDTO dto) { // <-- @Valid añadido
        
        UsuarioRegistroRespuestaDTO actualizado = usuarioService.actualizarUsuario(id, dto);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    // --- TU MÉTODO (Correcto) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}