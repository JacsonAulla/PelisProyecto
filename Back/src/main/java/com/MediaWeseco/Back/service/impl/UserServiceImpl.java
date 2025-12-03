package com.MediaWeseco.Back.service.impl;

import com.MediaWeseco.Back.dtos.UserResponseDto;
import com.MediaWeseco.Back.models.Rol;
import com.MediaWeseco.Back.models.Usuario;
import com.MediaWeseco.Back.repository.RolRepository;
import com.MediaWeseco.Back.repository.UsuarioRepository;
import com.MediaWeseco.Back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<Usuario> usuariosPage = usuarioRepository.findAll(pageable);
        return usuariosPage.map(this::mapToDto);
    }

    @Override
    public UserResponseDto toggleUserStatus(Long userId) {
        // 1. Obtenemos el usuario que queremos banear
        Usuario targetUser = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario objetivo no encontrado"));

        // 2. Obtenemos el email del usuario que está ejecutando la acción (TÚ)
        String currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();

        // 3. VALIDACIÓN: No te puedes banear a ti mismo
        if (targetUser.getEmail().equals(currentPrincipalName)) {
            throw new RuntimeException("¡No puedes bloquear tu propia cuenta!");
        }

        // 4. VALIDACIÓN: No puedes banear a otro ADMIN (Opcional, quítalo si no quieres
        // esta regla)
        boolean isTargetAdmin = targetUser.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ROLE_ADMIN"));

        if (isTargetAdmin) {
            throw new RuntimeException("No tienes permiso para bloquear a otro Administrador.");
        }

        // Si pasa las validaciones, procedemos
        targetUser.setEstaActivo(!targetUser.getEstaActivo());
        return mapToDto(usuarioRepository.save(targetUser));
    }

    @Override
    public UserResponseDto changeUserRole(Long userId, String newRoleName) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtenemos quién está haciendo la petición
        String currentPrincipalEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Si el usuario que intentas editar tiene el mismo email que tú... ¡ERROR!
        if (usuario.getEmail().equals(currentPrincipalEmail)) {
            throw new RuntimeException("OPERACIÓN DENEGADA: No puedes quitarte el rol de Admin a ti mismo.");
        }

        Rol nuevoRol = rolRepository.findByNombre(newRoleName)
                .orElseThrow(() -> new RuntimeException("Rol no existe: " + newRoleName));

        usuario.getRoles().clear();
        usuario.getRoles().add(nuevoRol);

        return mapToDto(usuarioRepository.save(usuario));
    }

    // Método auxiliar
    private UserResponseDto mapToDto(Usuario usuario) {
        String rolNombre = "SIN_ROL";
        if (!usuario.getRoles().isEmpty()) {
            rolNombre = usuario.getRoles().iterator().next().getNombre();
        }

        return UserResponseDto.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .username(usuario.getUsername())
                .imagenUrl(usuario.getImagenUrl())
                .provider(usuario.getProvider() != null ? usuario.getProvider().toString() : "LOCAL")
                .estaActivo(usuario.getEstaActivo())
                .estaVerificado(usuario.getEstaVerificado())
                .role(rolNombre)
                .build();
    }
}