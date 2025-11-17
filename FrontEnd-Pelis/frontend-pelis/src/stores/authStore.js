// src/stores/authStore.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
// 1. ¡Importamos las dos funciones de API!
import { loginUser, getMyProfile } from '@/services/api' 
import router from '@/router' 

export const useAuthStore = defineStore('auth', () => {
  
  // --- STATE ---
  const token = ref(localStorage.getItem('user_token'))
  // 2. ¡NUEVO ESTADO! Para guardar el objeto { id, nombre, email, rol }
  const user = ref(JSON.parse(localStorage.getItem('user_data')))

  // --- GETTERS ---
  const isLoggedIn = computed(() => !!token.value)
  
  // 3. ¡NUEVO GETTER! Para saber si somos Admin
  const isAdmin = computed(() => user.value?.rol === 'ADMIN')

  // --- ACTIONS ---

  /**
   * Acción de Login: Llama a la API, guarda el token Y busca el perfil.
   */
  async function login(usernameOrEmail, password) {
    // 1. Llama a la API de login
    const data = await loginUser(usernameOrEmail, password)
    
    // 2. Guarda el token en el estado y en localStorage
    token.value = data.tokenDeAcceso
    localStorage.setItem('user_token', data.tokenDeAcceso)
    
    // 3. ¡NUEVO PASO! Llama a fetchUserProfile para obtener los datos del usuario
    // (El interceptor de Axios ya sabe usar el token que acabamos de guardar)
    await fetchUserProfile()

    // 4. Redirige al inicio
    router.push('/')
  }

  /**
   * Acción de Logout: Limpia todo.
   */
  function logout() {
    token.value = null
    user.value = null // ¡NUEVO! Limpia el usuario
    localStorage.removeItem('user_token')
    localStorage.removeItem('user_data') // ¡NUEVO! Limpia los datos
    
    router.push('/login')
  }

  /**
   * 5. ¡NUEVA ACCIÓN! Busca el perfil del usuario y lo guarda.
   */
  async function fetchUserProfile() {
    if (!token.value) return // No intentes si no hay token

    try {
      const userData = await getMyProfile()
      user.value = userData
      // Guarda los datos en localStorage para "recordar" quién es
      localStorage.setItem('user_data', JSON.stringify(userData))
    } catch (error) {
      console.error("Error al buscar perfil:", error)
      // Si el token es inválido (ej. expiró), cerramos sesión
      logout()
    }
  }

  // --- Retornamos todo lo nuevo ---
  return {
    token,
    user, // Exponemos el objeto de usuario
    isLoggedIn,
    isAdmin, // ¡Exponemos el nuevo getter!
    login,
    logout,
    fetchUserProfile // Exponemos la nueva acción
  }
})