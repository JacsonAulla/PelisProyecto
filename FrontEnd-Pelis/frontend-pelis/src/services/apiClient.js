// src/services/apiClient.js
import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

// 1. CREAMOS LA INSTANCIA DE AXIOS
// Todas las peticiones de este cliente usarán el proxy de Vite
// (ej. /api/peliculas se convertirá en http://localhost:8080/api/peliculas)
const apiClient = axios.create({
  baseURL: '/api'
});

// 2. EL INTERCEPTOR MÁGICO
// Esto se ejecuta ANTES de que cada petición sea enviada.
apiClient.interceptors.request.use(
  (config) => {
    // 3. Obtenemos el authStore de Pinia
    // ¡Importante! El store DEBE instanciarse aquí dentro,
    // no puede ir arriba fuera de la función.
    const authStore = useAuthStore()

    // 4. Si estamos logueados, añadimos el token al header
    if (authStore.isLoggedIn) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }

    return config // Devolvemos la config (con el token ya puesto)
  },
  (error) => {
    // Esto es para errores en la creación de la petición
    return Promise.reject(error)
  }
);

// (Opcional pero recomendado: un interceptor de respuesta)
// Esto podría manejar errores 401 (token expirado)
// y llamar a authStore.logout() automáticamente.
// Lo podemos añadir después.

// 5. Exportamos la instancia para usarla en nuestro api.js
export default apiClient