package com.Proyecto.Peliculas.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.Proyecto.Peliculas.dtos.Usuario.AdminUsuarioCreateDTO;
import com.Proyecto.Peliculas.dtos.Usuario.AdminUsuarioUpdateDTO;
import com.Proyecto.Peliculas.dtos.Usuario.RegistroDTO;
import com.Proyecto.Peliculas.dtos.Usuario.UsuarioRegistroRespuestaDTO;

public interface UsuarioService extends UserDetailsService {

    UsuarioRegistroRespuestaDTO registrarUsuario(RegistroDTO registroDTO);

    // admin

    List<UsuarioRegistroRespuestaDTO> obtenerTodosLosUsuarios();

    UsuarioRegistroRespuestaDTO obtenerUsuarioPorId(Long id);

    UsuarioRegistroRespuestaDTO actualizarUsuario(Long id, AdminUsuarioUpdateDTO dto);

    void eliminarUsuario(Long id);

    UsuarioRegistroRespuestaDTO crearUsuarioAdmin(AdminUsuarioCreateDTO dto);

    UsuarioRegistroRespuestaDTO getMiPerfil();
    /*
     * @Transactional(readOnly = true)
     * public Optional<UsuarioDTO> obtenerPorUsername(String username) {
     * return usuarioRepository.findByUsername(username).map(this::toDTO);
     * }
     */
}
