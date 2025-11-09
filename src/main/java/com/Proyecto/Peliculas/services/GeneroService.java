package com.Proyecto.Peliculas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Proyecto.Peliculas.models.Genero;
import com.Proyecto.Peliculas.repository.GeneroRepository;

@Service
public class GeneroService {
    @Autowired
    private GeneroRepository generoRepository;

    @Transactional(readOnly = true)
    public List<Genero> obtenerTodos() {
        return generoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Genero> obtenerPorId(Long id) {
        return generoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Genero> obtenerPorNombre(String nombre) {
        return generoRepository.findByNombre(nombre);
    }

    public Genero crearGenero(Genero genero) {
        return generoRepository.save(genero);
    }

    public Genero actualizarGenero(Long id, Genero datosActualizados) {
        Optional<Genero> existente = generoRepository.findById(id);
        if (existente.isPresent()) {
            Genero gen = existente.get();
            if (datosActualizados.getNombre() != null && !datosActualizados.getNombre().trim().isEmpty()) {
                gen.setNombre(datosActualizados.getNombre());
            }
            return generoRepository.save(gen);
        }
        return null;
    }

    public void eliminarPorId(Long id) {
        generoRepository.deleteById(id);
    }
}