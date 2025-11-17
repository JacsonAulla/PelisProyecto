import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './style.css'

// 1. Importa tu store
import { useAuthStore } from './stores/authStore'

const pinia = createPinia()
const app = createApp(App)

app.use(router)
app.use(pinia) // 2. Importante que Pinia se use ANTES de instanciar el store

// --- ¡ESTA ES LA NUEVA LÓGICA! ---
// 3. Intenta cargar el perfil del usuario ANTES de montar la app
if (localStorage.getItem('user_token')) {
  const authStore = useAuthStore()
  // Usamos .then() en lugar de await porque no estamos en una función async
  authStore.fetchUserProfile().then(() => {
    app.mount('#app') // 4. Monta la app DESPUÉS de intentar cargar el perfil
  })
} else {
  app.mount('#app') // 5. Si no hay token, monta la app de inmediato
}