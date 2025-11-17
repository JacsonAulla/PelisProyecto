package com.Proyecto.Peliculas.services.impl;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Peliculas.dtos.Suscripcion.SuscripcionRequestDTO;
import com.Proyecto.Peliculas.dtos.Suscripcion.SuscripcionResponseDTO;
import com.Proyecto.Peliculas.enums.EstadoSuscripcion;
import com.Proyecto.Peliculas.exceptions.ResourceNotFoundException;
import com.Proyecto.Peliculas.models.Suscripcion;
import com.Proyecto.Peliculas.models.Usuario;
import com.Proyecto.Peliculas.repository.SuscripcionRepository;
import com.Proyecto.Peliculas.repository.UsuarioRepository;
import com.Proyecto.Peliculas.services.SuscripcionService;

@Service
public class SuscripcionServiceImpl implements SuscripcionService {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- MÉTODOS DEL USUARIO LOGUEADO ---

    @Override
    @Transactional(readOnly = true)
    public SuscripcionResponseDTO obtenerMiEstadoSuscripcion() {
        // 1. Obtiene el usuario del token
        Usuario usuario = obtenerUsuarioLogueado();
        
        // 2. Busca su suscripción más reciente
        Suscripcion suscripcion = suscripcionRepository.findFirstByUsuarioIdOrderByFechaInicioDesc(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Suscripcion", "usuarioId", usuario.getId()));
        
        // 3. Mapea a DTO y devuelve
        return mapEntidadToDTO(suscripcion);
    }

    @Override
    @Transactional
    public SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO requestDTO) {
        // 1. Obtiene el usuario del token
        Usuario usuario = obtenerUsuarioLogueado();

        // 2. Valida la regla de negocio (Trigger de BD también lo hace, pero validamos en servicio)
        suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA)
            .ifPresent(s -> {
                throw new RuntimeException("El usuario ya tiene una suscripción activa.");
            });
        
        // --- 3. (Lógica de Pago Simulado) ---
        // Aquí iría la lógica para procesar el 'requestDTO.getPaymentMethodToken()'
        // con Stripe, MercadoPago, etc.
        // Asumimos que el pago fue exitoso y costó 9.99.
        BigDecimal precioPagado = new BigDecimal("9.99");
        
        // 4. Crea la entidad Suscripcion
        Suscripcion nuevaSuscripcion = new Suscripcion();
        nuevaSuscripcion.setUsuario(usuario);
        nuevaSuscripcion.setFechaInicio(LocalDateTime.now());
        nuevaSuscripcion.setFechaFin(LocalDateTime.now().plusMonths(1)); // Válida por 1 mes
        nuevaSuscripcion.setEstado(EstadoSuscripcion.ACTIVA);
        nuevaSuscripcion.setPrecioPagado(precioPagado);
        
        // 5. Guarda en BD
        Suscripcion suscripcionGuardada = suscripcionRepository.save(nuevaSuscripcion);
        
        // 6. Mapea a DTO y devuelve
        return mapEntidadToDTO(suscripcionGuardada);
    }

    @Override
    @Transactional
    public SuscripcionResponseDTO cancelarSuscripcion() {
        // 1. Obtiene el usuario del token
        Usuario usuario = obtenerUsuarioLogueado();
        
        // 2. Busca su suscripción ACTIVA
        Suscripcion suscripcionActiva = suscripcionRepository.findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripcion", "estado", 0L)); // 0L como placeholder

        // 3. Cambia el estado y la fecha de fin
        suscripcionActiva.setEstado(EstadoSuscripcion.CANCELADA);
        suscripcionActiva.setFechaFin(LocalDateTime.now()); // Se cancela inmediatamente
        
        // 4. Guarda los cambios
        Suscripcion suscripcionCancelada = suscripcionRepository.save(suscripcionActiva);
        
        // 5. Mapea a DTO y devuelve
        return mapEntidadToDTO(suscripcionCancelada);
    }

    // --- MÉTODO DE ADMIN ---

    @Override
    @Transactional(readOnly = true)
    public Page<SuscripcionResponseDTO> obtenerTodasLasSuscripciones(Pageable pageable) {
        Page<Suscripcion> suscripciones = suscripcionRepository.findAll(pageable);
        return suscripciones.map(this::mapEntidadToDTO);
    }

    // --- MÉTODOS HELPER (Privados) ---

    /**
     * Obtiene el objeto Usuario del usuario que está actualmente logueado
     * (a través del token JWT en el Contexto de Seguridad).
     */
    private Usuario obtenerUsuarioLogueado() {
        // 1. Obtiene el 'Authentication' del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 2. Extrae el "principal" (el username/email que guardamos en el token)
        String emailUsuario = authentication.getName();
        
        // 3. Busca al usuario en la BD por su email
        return usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en el token: " + emailUsuario));
    }
    
    /**
     * Mapea una Entidad Suscripcion a su DTO de respuesta.
     */
    private SuscripcionResponseDTO mapEntidadToDTO(Suscripcion suscripcion) {
        SuscripcionResponseDTO dto = new SuscripcionResponseDTO();
        dto.setId(suscripcion.getId());
        dto.setFechaInicio(suscripcion.getFechaInicio());
        dto.setFechaFin(suscripcion.getFechaFin());
        dto.setEstado(suscripcion.getEstado());
        dto.setPrecioPagado(suscripcion.getPrecioPagado());
        return dto;
    }
}