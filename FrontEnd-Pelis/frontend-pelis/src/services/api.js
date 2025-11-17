// src/services/api.js
import apiClient from './apiClient' // ¡Importamos nuestro nuevo cliente Axios!

/**
 * Un helper para manejar los errores de Axios.
 * Axios lanza un error si el status no es 2xx, pero el error
 * puede tener el mensaje del backend en `error.response.data`.
 */
function handleError(error) {
  // 1. Revisa si el error viene de una respuesta del backend
  if (error.response && error.response.data) {
    
    // 2. Revisa si el backend nos dio un objeto de error (ej. { "message": "..." })
    if (error.response.data.message) {
      throw new Error(error.response.data.message);
    }
    
    // 3. Revisa si el backend nos dio un error de validación (ej. { "email": "..." })
    // (Esto convierte { "email": "formato inválido" } en "email: formato inválido")
    if (typeof error.response.data === 'object') {
      const messages = Object.entries(error.response.data)
        .map(([key, value]) => `${key}: ${value}`)
        .join(', ');
      if (messages) {
        throw new Error(messages);
      }
    }
    
    // Si no, lanza el error de data (si es un string)
    throw new Error(error.response.data);
  }
  
  // 4. Si es un error de red o de configuración de Axios
  throw new Error(error.message);
}


/**
 * Llama al endpoint de Registro.
 * POST /api/auth/register
 * @param {object} registroDTO El DTO de registro (nombre, apellido, etc.)
 */
export async function registerUser(registroDTO) {
  try {
    // Usamos apiClient.post
    // Axios automáticamente envía el DTO como JSON
    const response = await apiClient.post('/auth/register', registroDTO);
    return response.data; // Axios nos da el JSON en 'response.data'
  } catch (error) {
    handleError(error); // Dejamos que el helper maneje el error
  }
}

/**
 * Llama al endpoint de Login.
 * POST /api/auth/login
 * @param {string} usernameOrEmail
 * @param {string} password
 */
export async function loginUser(usernameOrEmail, password) {
  try {
    const response = await apiClient.post('/auth/login', { 
      usernameOrEmail, 
      password 
    });
    // Esto devolverá {"tokenDeAcceso": "...", "tipoDeToken": "Bearer"}
    return response.data;
  } catch (error) {
    handleError(error);
  }
}


/**
 * Obtiene el catálogo público de películas (paginado).
 * GET /api/peliculas
 */
export async function getPublicPeliculas(page = 0, size = 100, sort = 'anioLanzamiento,desc') {
  try {
    const response = await apiClient.get('/peliculas', {
      params: { // Axios maneja los query params de forma limpia
        page,
        size,
        sort
      }
    });
    // La API de paginación devuelve el array en 'content'
    return response.data.content;
  } catch (error) {
    handleError(error);
  }
}

export async function getMiBiblioteca(page = 0, size = 20) {
  try {
    const response = await apiClient.get('/biblioteca/mi-biblioteca', {
      params: {
        page,
        size,
        sort: 'fechaCompra,desc' // Ordenamos por más reciente
      }
    });
    // Devuelve el objeto de página completo
    return response.data;
  } catch (error) {
    handleError(error);
  }
}

export async function createOrden(peliculasId) {
  try {
    // El token "tok_simulado_ok" es el placeholder que espera tu backend
    const requestBody = {
      peliculasId: peliculasId,
      paymentMethodToken: "tok_simulado_ok" 
    };
    
    const response = await apiClient.post('/ordenes/crear', requestBody);
    
    // Devuelve la orden creada
    return response.data;
  } catch (error) {
    handleError(error);
  }
}

export async function getPeliculaDetalle(id) {
  try {
    const response = await apiClient.get(`/peliculas/${id}`);
    
    // Devuelve el objeto PeliculaDetalleDTO completo
    return response.data;
  } catch (error) {
    handleError(error);
  }
}

export async function getMiSuscripcion() {
  try {
    const response = await apiClient.get('/suscripciones/mi-estado');
    return response.data; // Devuelve el SuscripcionResponseDTO
  } catch (error) {
    // Si da un 404 (porque el usuario nunca se ha suscrito),
    // lo manejamos como un error "no encontrado" en lugar de fallar.
    if (error.response && error.response.status === 404) {
      return null; // Devuelve null si no hay historial de suscripción
    }
    handleError(error);
  }
}

export async function createSuscripcion() {
  try {
    // El token simulado que espera el backend
    const requestBody = {
      paymentMethodToken: "tok_simulado_suscripcion_ok"
    };
    const response = await apiClient.post('/suscripciones/crear', requestBody);
    return response.data; // Devuelve el nuevo SuscripcionResponseDTO
  } catch (error) {
    handleError(error);
  }
}

export async function cancelSuscripcion() {
  try {
    // Este endpoint no necesita body, solo la autenticación
    const response = await apiClient.post('/suscripciones/cancelar');
    return response.data; // Devuelve el SuscripcionResponseDTO actualizado (cancelado)
  } catch (error) {
    handleError(error);
  }
}

export async function getMyProfile() {
  try {
    const response = await apiClient.get('/auth/me');
    return response.data; // Devuelve el UsuarioRegistroRespuestaDTO
  } catch (error) {
    handleError(error);
  }
}

// --- =================================== ---
// ---         FUNCIONES DE ADMIN          ---
// --- =================================== ---

// --- CRUD de Usuarios ---

/**
 * [Admin] Obtiene la lista completa de todos los usuarios.
 * GET /api/admin/usuarios
 */
export async function getAllUsers() {
  try {
    const response = await apiClient.get('/admin/usuarios');
    return response.data; // Devuelve un array de UsuarioRegistroRespuestaDTO
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Obtiene un usuario específico por su ID.
 * GET /api/admin/usuarios/{id}
 */
export async function getUserById(id) {
  try {
    const response = await apiClient.get(`/admin/usuarios/${id}`);
    return response.data; // Devuelve un UsuarioRegistroRespuestaDTO
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Crea un nuevo usuario.
 * POST /api/admin/usuarios
 * @param {object} createDTO El AdminUsuarioCreateDTO
 */
export async function createUser(createDTO) {
  try {
    const response = await apiClient.post('/admin/usuarios', createDTO);
    return response.data; // Devuelve el DTO del usuario creado
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Actualiza un usuario existente.
 * PUT /api/admin/usuarios/{id}
 * @param {number} id El ID del usuario a actualizar
 * @param {object} updateDTO El AdminUsuarioUpdateDTO
 */
export async function updateUser(id, updateDTO) {
  try {
    const response = await apiClient.put(`/admin/usuarios/${id}`, updateDTO);
    return response.data; // Devuelve el DTO del usuario actualizado
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Elimina un usuario por su ID.
 * DELETE /api/admin/usuarios/{id}
 */
export async function deleteUser(id) {
  try {
    // DELETE no devuelve contenido (204 No Content), por eso no hay response.data
    await apiClient.delete(`/admin/usuarios/${id}`);
    return null; 
  } catch (error) {
    handleError(error);
  }
}

// --- =================================== ---
// ---         CRUD DE PELÍCULAS           ---
// --- =================================== ---

/**
 * [Admin] Obtiene la lista PAGINADA de películas para el dashboard.
 * GET /api/peliculas
 */
export async function getMoviesAdmin(page = 0, size = 10, sort = 'id,desc') {
  try {
    const response = await apiClient.get('/peliculas', {
      params: { // Axios maneja los query params de forma limpia
        page,
        size,
        sort
      }
    });
    // Devuelve el objeto de página completo
    return response.data; 
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Crea una nueva película.
 * POST /api/peliculas
 * @param {object} movieRequestDTO El PeliculaRequestDTO
 */
export async function createMovie(movieRequestDTO) {
  try {
    const response = await apiClient.post('/peliculas', movieRequestDTO);
    return response.data; // Devuelve PeliculaDetalleDTO
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Actualiza una película existente.
 * PUT /api/peliculas/{id}
 * @param {number} id El ID de la película a actualizar
 * @param {object} movieRequestDTO El PeliculaRequestDTO
 */
export async function updateMovie(id, movieRequestDTO) {
  try {
    const response = await apiClient.put(`/peliculas/${id}`, movieRequestDTO);
    return response.data; // Devuelve PeliculaDetalleDTO
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Elimina una película por su ID.
 * DELETE /api/peliculas/{id}
 */
export async function deleteMovie(id) {
  try {
    await apiClient.delete(`/peliculas/${id}`);
    return null; 
  } catch (error) {
    handleError(error);
  }
}

// --- =================================== ---
// ---          CRUD DE GÉNEROS            ---
// --- =================================== ---

/**
 * Obtiene la lista completa de géneros (para dropdowns y tabla).
 * GET /api/generos
 */
export async function getAllGenres() {
  try {
    const response = await apiClient.get('/generos');
    return response.data; // Devuelve List<GeneroDTO>
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Crea un nuevo género.
 * POST /api/generos
 * @param {object} genreDTO El GeneroDTO (con nombre)
 */
export async function createGenre(genreDTO) {
  try {
    const response = await apiClient.post('/generos', genreDTO);
    return response.data; // Devuelve el GeneroDTO creado
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Actualiza un género existente.
 * PUT /api/generos/{id}
 * @param {number} id El ID del género a actualizar
 * @param {object} genreDTO El GeneroDTO (con nombre)
 */
export async function updateGenre(id, genreDTO) {
  try {
    const response = await apiClient.put(`/generos/${id}`, genreDTO);
    return response.data; // Devuelve el GeneroDTO actualizado
  } catch (error) {
    handleError(error);
  }
}

/**
 * [Admin] Elimina un género por su ID.
 * DELETE /api/generos/{id}
 */
export async function deleteGenre(id) {
  try {
    await apiClient.delete(`/generos/${id}`);
    return null; 
  } catch (error) {
    handleError(error);
  }
}