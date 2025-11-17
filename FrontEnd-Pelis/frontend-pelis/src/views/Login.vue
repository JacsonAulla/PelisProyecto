<template>
  <div class="login-page">
    <div class="login-content">
      <div class="card bg-dark text-white border-secondary">
        <div class="card-body">
          <!-- Logo Section -->
          <div class="logo-section text-center mb-4">
            <i class="fa-solid fa-film logo-icon"></i>
            <h2 class="logo-title">StreamsUTP</h2>
          </div>

          <!-- Title -->
          <h3 class="card-title text-center mb-4">
            <i class="fa-solid fa-sign-in-alt me-2"></i>
            Iniciar Sesión
          </h3>
          
          <!-- Form -->
          <form @submit.prevent="handleLogin">
            
            <!-- Username/Email -->
            <div class="mb-3">
              <label for="usernameOrEmail" class="form-label">
                <i class="fa-solid fa-user me-1"></i>
                Usuario o Email
              </label>
              <input 
                type="text" 
                class="form-control"
                :class="{ 'is-invalid': error && !usernameOrEmail }"
                id="usernameOrEmail" 
                v-model="usernameOrEmail"
                placeholder="tu_usuario@email.com"
                required
                autocomplete="username"
              />
            </div>

            <!-- Password -->
            <div class="mb-3">
              <label for="password" class="form-label">
                <i class="fa-solid fa-lock me-1"></i>
                Contraseña
              </label>
              <div class="password-group">
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  class="form-control"
                  :class="{ 'is-invalid': error && !password }"
                  id="password" 
                  v-model="password"
                  placeholder="••••••••"
                  required
                  autocomplete="current-password"
                />
                <button 
                  type="button" 
                  class="btn-toggle-password"
                  @click="showPassword = !showPassword"
                  tabindex="-1"
                  title="Mostrar/Ocultar contraseña"
                >
                  <i :class="showPassword ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'"></i>
                </button>
              </div>
            </div>

            <!-- Error Message -->
            <div v-if="error" class="alert alert-danger mt-3 mb-3">
              <i class="fa-solid fa-exclamation-circle me-2"></i>
              <span>{{ error }}</span>
            </div>

            <!-- Submit Button -->
            <button 
              type="submit" 
              class="btn btn-primary btn-login w-100"
              :disabled="cargando || !usernameOrEmail || !password"
            >
              <span v-if="cargando" class="spinner-border spinner-border-sm me-2"></span>
              <span>{{ cargando ? 'Verificando...' : 'Entrar' }}</span>
            </button>

            <!-- Register Link -->
            <div class="text-center mt-4">
              <small class="text-muted">
                ¿No tienes cuenta? 
                <router-link to="/register" class="text-primary fw-bold text-decoration-none">
                  Regístrate aquí
                </router-link>
              </small>
            </div>

          </form>
        </div>
      </div>

      <!-- Footer Text -->
      <div class="footer-text text-center mt-3">
        <small class="text-muted">
          <i class="fa-solid fa-shield me-1"></i>
          Tu información está protegida
        </small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'

// Estado reactivo
const usernameOrEmail = ref('')
const password = ref('')
const showPassword = ref(false)
const cargando = ref(false)
const error = ref(null)

const authStore = useAuthStore()

const handleLogin = async () => {
  // Validación básica
  if (!usernameOrEmail.value?.trim()) {
    error.value = 'El usuario/email es requerido'
    return
  }
  if (!password.value?.trim()) {
    error.value = 'La contraseña es requerida'
    return
  }

  cargando.value = true
  error.value = null
  
  try {
    await authStore.login(usernameOrEmail.value, password.value)
    // El store se encarga de redirect
  } catch (err) {
    error.value = err.message || 'Error en la autenticación'
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped>
/* ===================== VARIABLES CSS ===================== */

:root {
  --primary-color: #0b75ff;
  --danger-color: #ff2b2b;
  --dark-bg: #1a1a1a;
  --card-bg: #2b2b2b;
  --border-color: #444;
  --text-muted: #999;
}

/* ===================== CONTENEDOR PRINCIPAL ===================== */

.login-page {
  width: 100%;
  height: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 1rem;
}

.login-content {
  width: 100%;
  max-width: 420px;
  display: flex;
  flex-direction: column;
}

/* ===================== TARJETA PRINCIPAL ===================== */

.card {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  animation: slideUp 0.5s ease-out;
  background: var(--card-bg) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-body {
  padding: 2rem 1.5rem;
}

/* ===================== LOGO SECTION ===================== */

.logo-section {
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-icon {
  font-size: 2.5rem;
  color: var(--primary-color);
  display: block;
  margin-bottom: 0.5rem;
}

.logo-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  letter-spacing: 1px;
}

/* ===================== TÍTULO ===================== */

.card-title {
  font-weight: 700;
  color: #fff;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

/* ===================== LABELS ===================== */

.form-label {
  color: #ccc;
  font-weight: 500;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

/* ===================== INPUTS ===================== */

.form-control {
  background-color: var(--dark-bg);
  border: 1.5px solid var(--border-color) !important;
  color: #fff;
  padding: 0.75rem;
  border-radius: 6px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-control:focus {
  background-color: var(--dark-bg);
  border-color: var(--primary-color) !important;
  color: #fff;
  box-shadow: 0 0 0 0.2rem rgba(11, 117, 255, 0.15) !important;
  outline: none;
}

.form-control::placeholder {
  color: #666;
}

.form-control.is-invalid {
  border-color: var(--danger-color) !important;
  background-color: rgba(255, 43, 43, 0.05);
}

/* ===================== PASSWORD GROUP ===================== */

.password-group {
  position: relative;
  display: flex;
  align-items: center;
}

.password-group .form-control {
  flex: 1;
  padding-right: 2.5rem;
}

.btn-toggle-password {
  position: absolute;
  right: 0.75rem;
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0.5rem;
  font-size: 1rem;
  transition: color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.btn-toggle-password:hover {
  color: var(--primary-color);
}

/* ===================== ALERT ===================== */

.alert {
  border-radius: 6px;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.alert-danger {
  background-color: rgba(255, 43, 43, 0.1);
  border: 1px solid var(--danger-color);
  color: #ff6b6b;
}

.alert-danger i {
  font-size: 1.1rem;
  flex-shrink: 0;
}

/* ===================== BOTÓN PRINCIPAL ===================== */

.btn-login {
  background-color: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  color: #fff;
  font-weight: 600;
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  border-radius: 6px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  cursor: pointer;
  border: none;
}

.btn-login:hover:not(:disabled) {
  background-color: #0a5fd8 !important;
  border-color: #0a5fd8 !important;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
  transform: translateY(-2px);
}

.btn-login:active:not(:disabled) {
  transform: translateY(0);
}

.btn-login:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
  border-width: 0.2em;
}

/* ===================== LINKS ===================== */

.text-primary {
  color: var(--primary-color) !important;
  transition: opacity 0.2s;
}

.text-primary:hover {
  opacity: 0.8;
}

.text-muted {
  color: #999 !important;
}

/* ===================== FOOTER TEXT ===================== */

.footer-text {
  animation: fadeIn 0.8s ease-out 0.3s both;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* ===================== RESPONSIVE BREAKPOINTS ===================== */

/* MOBILE (Predeterminado) */
@media (max-width: 576px) {
  .login-page {
    padding: 1rem;
  }

  .login-content {
    max-width: 100%;
  }

  .card-body {
    padding: 1.5rem 1rem;
  }

  .logo-title {
    font-size: 1.5rem;
  }

  .card-title {
    font-size: 1.3rem;
    margin-bottom: 1.25rem;
  }

  .form-label {
    font-size: 0.9rem;
  }

  .form-control {
    font-size: 16px; /* Previene zoom en iOS */
    padding: 0.65rem;
  }

  .btn-login {
    padding: 0.7rem 1.25rem;
    font-size: 0.95rem;
  }

  .alert {
    font-size: 0.9rem;
    padding: 0.6rem 0.8rem;
  }

  .text-center small {
    font-size: 0.85rem;
  }
}

/* TABLET (576px - 768px) */
@media (min-width: 576px) and (max-width: 768px) {
  .card-body {
    padding: 2rem 1.75rem;
  }

  .login-content {
    max-width: 450px;
  }
}

/* DESKTOP (768px+) */
@media (min-width: 768px) {
  .login-content {
    max-width: 450px;
  }

  .card-body {
    padding: 2.5rem 2rem;
  }

  .logo-icon {
    font-size: 3rem;
  }

  .logo-title {
    font-size: 2rem;
  }

  .card-title {
    font-size: 1.6rem;
  }
}

/* LARGE SCREENS (1024px+) */
@media (min-width: 1024px) {
  .login-content {
    max-width: 500px;
  }

  .card {
    box-shadow: 0 12px 48px rgba(0, 0, 0, 0.8);
  }

  .btn-login:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 8px 24px rgba(11, 117, 255, 0.4);
  }
}

/* EXTRA LARGE (1440px+) */
@media (min-width: 1440px) {
  .login-content {
    max-width: 550px;
  }
}
</style>
