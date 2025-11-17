package com.Proyecto.Peliculas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaDetalleDTO;
import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaRequestDTO;
import com.Proyecto.Peliculas.dtos.Peliculas.PeliculaResumenDTO;

public interface PeliculaService {

    //--- Para el usuario ---

    //Obtiene una lista paginada de todas las películas (en formato ligero).
    Page<PeliculaResumenDTO> obtenerCatalogoPaginado(Pageable pageable);

    //Obtiene los detalles completos de una sola película.
    PeliculaDetalleDTO obtenerPeliculaPorId(Long id);

    //Busca películas por un término en el título (paginado).
    Page<PeliculaResumenDTO> buscarPeliculasPorTitulo(String titulo, Pageable pageable);

    //Busca películas por un ID de género (paginado).
    Page<PeliculaResumenDTO> buscarPeliculasPorGenero(Long generoId, Pageable pageable);


    // --- MÉTODOS DE ADMIN (CRUD) ---

    //Crea una nueva película en la base de datos
    PeliculaDetalleDTO crearPelicula(PeliculaRequestDTO peliculaRequestDTO);

    //Actualiza una película existente
    PeliculaDetalleDTO actualizarPelicula(Long id, PeliculaRequestDTO peliculaRequestDTO);

    //Elimina una película por su ID
    void eliminarPelicula(Long id);


    /*
    @Autowired
    private PeliculaRepository peliculaRepository;

    

    // para que el usuario vea todas las peliculas(id, imgfrente y titulo)
    @Transactional(readOnly = true)
    public List<PeliculaUsuarioDTO> obtenerTodasParaUsuario() {
        List<Pelicula> peliculas = peliculaRepository.findAll();
        List<PeliculaUsuarioDTO> dtos = new ArrayList<>();
        
        for (Pelicula pelicula : peliculas) {
            PeliculaUsuarioDTO dto = new PeliculaUsuarioDTO();
            dto.setId(pelicula.getId());
            dto.setTitulo(pelicula.getTitulo());
            dto.setImgFrente(pelicula.getImgFrente());
            dtos.add(dto);
        }
        
        return dtos;
    }

    // para que el usuario busque por titulo
    @Transactional(readOnly = true)
    public Optional<PeliculaUsuarioDTO> obtenerPorTituloParaUsuario(String titulo) {
        Optional<Pelicula> pelicula = peliculaRepository.findByTitulo(titulo);
        
        if (pelicula.isPresent()) {
            PeliculaUsuarioDTO dto = new PeliculaUsuarioDTO();
            dto.setId(pelicula.get().getId());
            dto.setTitulo(pelicula.get().getTitulo());
            dto.setImgFrente(pelicula.get().getImgFrente());
            return Optional.of(dto);
        }
        
        return Optional.empty();
    }

    //--- Para el admin ---

    // igual que el del usuario pero ahora se ven todo de la pelicula
    @Transactional(readOnly = true)
    public List<PeliculaAdminDTO> obtenerTodasParaAdmin() {
        List<Pelicula> peliculas = peliculaRepository.findAll();
        List<PeliculaAdminDTO> dtos = new ArrayList<>();
        
        for (Pelicula pelicula : peliculas) {
            dtos.add(convertirAPeliculaAdminDTO(pelicula));
        }
        
        return dtos;
    }

    // esto para busqueda por disponible
    @Transactional(readOnly = true)
    public List<PeliculaAdminDTO> obtenerDisponibles() {
        List<Pelicula> peliculas = peliculaRepository.findByDisponibleTrue();
        List<PeliculaAdminDTO> dtos = new ArrayList<>();
        
        for (Pelicula pelicula : peliculas) {
            dtos.add(convertirAPeliculaAdminDTO(pelicula));
        }
        
        return dtos;
    }

    // busqueda por id
    @Transactional(readOnly = true)
    public Optional<PeliculaAdminDTO> obtenerPorIdAdmin(Long id) {
        Optional<Pelicula> pelicula = peliculaRepository.findById(id);
        
        if (pelicula.isPresent()) {
            return Optional.of(convertirAPeliculaAdminDTO(pelicula.get()));
        }
        
        return Optional.empty();
    }

    // para que el admin cree una pelicula
    public Pelicula crearPelicula(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }
    
    // para que el admin actualice una pelicula
    public Pelicula actualizarPelicula(Long id, Pelicula peliculaActualizada) {
        Optional<Pelicula> peliculaExistente = peliculaRepository.findById(id);
        if (peliculaExistente.isPresent()) {
            Pelicula pelicula = peliculaExistente.get();
            
            if (peliculaActualizada.getTitulo() != null && !peliculaActualizada.getTitulo().trim().isEmpty()) {
                pelicula.setTitulo(peliculaActualizada.getTitulo());
            }
            if (peliculaActualizada.getDescripcion() != null && !peliculaActualizada.getDescripcion().trim().isEmpty()) {
                pelicula.setDescripcion(peliculaActualizada.getDescripcion());
            }
            if (peliculaActualizada.getAnioLanzamiento() != null) {
                pelicula.setAnioLanzamiento(peliculaActualizada.getAnioLanzamiento());
            }
            if (peliculaActualizada.getDuracionMinutos() != null) {
                pelicula.setDuracionMinutos(peliculaActualizada.getDuracionMinutos());
            }
            if (peliculaActualizada.getDisponible() != null) {
                pelicula.setDisponible(peliculaActualizada.getDisponible());
            }
            if (peliculaActualizada.getPrecioComprar() != null) {
                pelicula.setPrecioComprar(peliculaActualizada.getPrecioComprar());
            }
            if (peliculaActualizada.getImgFrente() != null && !peliculaActualizada.getImgFrente().trim().isEmpty()) {
                pelicula.setImgFrente(peliculaActualizada.getImgFrente());
            }
            if (peliculaActualizada.getUrlStream() != null && !peliculaActualizada.getUrlStream().trim().isEmpty()) {
                pelicula.setUrlStream(peliculaActualizada.getUrlStream());
            }
            if (peliculaActualizada.getGeneros() != null && !peliculaActualizada.getGeneros().isEmpty()) {
                pelicula.setGeneros(peliculaActualizada.getGeneros());
            }
            
            return peliculaRepository.save(pelicula);
        }
        return null;
    }

    // eliminar por id
    public void eliminarPelicula(Long id) {
        peliculaRepository.deleteById(id);
    }
    
    // eliminar pelicula
    public void eliminar(Pelicula pelicula) {
        peliculaRepository.delete(pelicula);
    }

    // MÉTODO PÚBLICO para convertir Pelicula a PeliculaAdminDTO
    public PeliculaAdminDTO convertirAPeliculaAdminDTO(Pelicula pelicula) {
        PeliculaAdminDTO dto = new PeliculaAdminDTO();
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setDescripcion(pelicula.getDescripcion());
        dto.setAnioLanzamiento(pelicula.getAnioLanzamiento());
        dto.setDuracionMinutos(pelicula.getDuracionMinutos());
        dto.setDisponible(pelicula.getDisponible());
        dto.setPrecioComprar(pelicula.getPrecioComprar());
        dto.setImgFrente(pelicula.getImgFrente());
        dto.setUrlStream(pelicula.getUrlStream());
        
        // Convertir géneros
        List<GeneroDTO> generosDTO = pelicula.getGeneros().stream()
            .map(g -> new GeneroDTO(g.getId(), g.getNombre()))
            .collect(Collectors.toList());
        dto.setGeneros(generosDTO);
        
        return dto;
    }
    */
}