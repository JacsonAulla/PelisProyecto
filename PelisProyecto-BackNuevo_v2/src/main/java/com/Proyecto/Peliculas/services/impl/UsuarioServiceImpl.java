package com.Proyecto.Peliculas.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Proyecto.Peliculas.dtos.Usuario.AdminUsuarioCreateDTO;
import com.Proyecto.Peliculas.dtos.Usuario.AdminUsuarioUpdateDTO;
import com.Proyecto.Peliculas.dtos.Usuario.RegistroDTO;
import com.Proyecto.Peliculas.dtos.Usuario.UsuarioRegistroRespuestaDTO;
import com.Proyecto.Peliculas.enums.TipoRol; // Importante
import com.Proyecto.Peliculas.exceptions.ResourceNotFoundException;
import com.Proyecto.Peliculas.models.Usuario;
import com.Proyecto.Peliculas.repository.UsuarioRepository;
import com.Proyecto.Peliculas.services.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UsuarioRegistroRespuestaDTO registrarUsuario(RegistroDTO registroDTO) {
        if (usuarioRepository.existsByUsername(registroDTO.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setApellido(registroDTO.getApellido());
        usuario.setUsername(registroDTO.getUsername());
        usuario.setEmail(registroDTO.getEmail());

        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setRol(TipoRol.USUARIO);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        UsuarioRegistroRespuestaDTO respuesta = new UsuarioRegistroRespuestaDTO();
        respuesta.setId(usuarioGuardado.getId());
        respuesta.setNombre(usuarioGuardado.getNombre());
        respuesta.setApellido(usuarioGuardado.getApellido());
        respuesta.setUsername(usuarioGuardado.getUsername());
        respuesta.setEmail(usuarioGuardado.getEmail());
        respuesta.setRol(usuarioGuardado.getRol());

        return respuesta;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con username o email: " + usernameOrEmail));

        Collection<? extends GrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(usuario.getRol().name()));

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                authorities);
    }

    @Override
    public List<UsuarioRegistroRespuestaDTO> obtenerTodosLosUsuarios() {
        // 1. Busca todos los usuarios en la BD
        List<Usuario> usuarios = usuarioRepository.findAll();

        // 2. Mapea la lista de Entidades a una lista de DTOs de respuesta
        return usuarios.stream()
                .map(this::mapUsuarioToDto) // Usa un helper para convertir
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioRegistroRespuestaDTO obtenerUsuarioPorId(Long id) {
        // 1. Busca al usuario por ID o lanza una excepción si no lo encuentra
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // 2. Convierte la Entidad a DTO y lo devuelve
        return mapUsuarioToDto(usuario);
    }

    @Override
    public UsuarioRegistroRespuestaDTO actualizarUsuario(Long id, AdminUsuarioUpdateDTO dto) {
        // 1. Busca al usuario que se va a actualizar
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // 2. --- VALIDACIÓN DE DUPLICADOS (AÑADIDA) ---
        // Verifica si el 'username' cambió Y si el nuevo ya existe
        if (dto.getUsername() != null && !dto.getUsername().equals(usuario.getUsername())) {
            if (usuarioRepository.existsByUsername(dto.getUsername())) {
                throw new RuntimeException("Error: El nombre de usuario ya está en uso.");
            }
            usuario.setUsername(dto.getUsername());
        }

        // Verifica si el 'email' cambió Y si el nuevo ya existe
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Error: El email ya está en uso.");
            }
            usuario.setEmail(dto.getEmail());
        }

        // 3. Actualiza el resto de campos desde el DTO
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setRol(dto.getRol()); // El admin puede cambiar el rol

        // 4. Guarda los cambios en la BD
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        // 5. Devuelve la respuesta DTO
        return mapUsuarioToDto(usuarioActualizado);
    }

    @Override
    public void eliminarUsuario(Long id) {
        // 1. Busca al usuario para asegurarse de que existe
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // 2. Lo elimina
        usuarioRepository.delete(usuario);
    }

    // UsuarioServiceImpl.java (añadir este método)
    @Override
    public UsuarioRegistroRespuestaDTO crearUsuarioAdmin(AdminUsuarioCreateDTO dto) {
        // Reutilizamos las mismas validaciones de 'registrarUsuario'
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Hashear

        // La diferencia clave: tomamos el rol del DTO
        usuario.setRol(dto.getRol());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return mapUsuarioToDto(usuarioGuardado);
    }

    // --- HELPER PRIVADO ---

    /**
     * Convierte una Entidad Usuario a un UsuarioRespuestaDTO (sin contraseña).
     * Lo creamos para no repetir este código en 4 métodos distintos (DRY).
     */
    private UsuarioRegistroRespuestaDTO mapUsuarioToDto(Usuario usuario) {
        UsuarioRegistroRespuestaDTO dto = new UsuarioRegistroRespuestaDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        return dto;
    }

    @Override
    public UsuarioRegistroRespuestaDTO getMiPerfil() {
        // 1. Obtiene la información de autenticación del "contexto de seguridad"
        // (Esto lo puso nuestro JwtAuthenticationFilter)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Extrae el "username" (que en nuestro caso es el email)
        // (Lo sabemos porque en tu loadUserByUsername usaste: new
        // User(usuario.getEmail(), ...))
        String email = authentication.getName();

        // 3. Busca al usuario en la BD usando el email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en el token: " + email));

        // 4. Convierte la Entidad a DTO (usando tu helper) y lo devuelve
        return mapUsuarioToDto(usuario);
    }

}
