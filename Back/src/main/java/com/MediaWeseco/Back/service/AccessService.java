package com.MediaWeseco.Back.service;

import com.MediaWeseco.Back.models.Contenido;
import com.MediaWeseco.Back.models.Suscripcion;
import com.MediaWeseco.Back.repository.BibliotecaRepository;
import com.MediaWeseco.Back.repository.ContenidoRepository;
import com.MediaWeseco.Back.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final BibliotecaRepository bibliotecaRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final ContenidoRepository contenidoRepository;

    public boolean tieneAcceso(Long usuarioId, Long contenidoId) {

        Contenido contenido = contenidoRepository.findById(contenidoId).orElseThrow();
        if (contenido.getPrecio() != null && contenido.getPrecio().compareTo(BigDecimal.ZERO) == 0) {
        }

        if (bibliotecaRepository.existsByUsuarioIdAndContenidoId(usuarioId, contenidoId)) {
            return true;
        }

        Optional<Suscripcion> sus = suscripcionRepository.findActiveSubscription(usuarioId);
        return sus.isPresent();
    }
}