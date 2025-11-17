<template>
  <div class="register-page">
    <div class="register-content">
      <div class="card bg-dark text-white border-secondary">
        <div class="card-body">
          <!-- Logo Section -->
          <div class="logo-section text-center mb-4">
            <i class="fa-solid fa-film logo-icon"></i>
            <h2 class="logo-title">StreamsUTP</h2>
          </div>

          <!-- Title -->
          <h3 class="card-title text-center mb-4">
            <i class="fa-solid fa-user-plus me-2"></i>
            Crear Cuenta
          </h3>
          
          <!-- Form -->
          <form @submit.prevent="handleRegister">

            <!-- Nombre & Apellido (Row) -->
            <div class="row">
              <div class="col-12 col-md-6 mb-3">
                <label for="nombre" class="form-label">
                  <i class="fa-solid fa-user me-1"></i>
                  Nombre
                </label>
                <input 
                  type="text" 
                  class="form-control"
                  :class="{ 'is-invalid': error && !form.nombre }"
                  id="nombre" 
                  v-model="form.nombre"
                  placeholder="Tu nombre"
                  required
                />
              </div>
              <div class="col-12 col-md-6 mb-3">
                <label for="apellido" class="form-label">
                  <i class="fa-solid fa-user me-1"></i>
                  Apellido
                </label>
                <input 
                  type="text" 
                  class="form-control"
                  :class="{ 'is-invalid': error && !form.apellido }"
                  id="apellido" 
                  v-model="form.apellido"
                  placeholder="Tu apellido"
                  required
                />
              </div>
            </div>

            <!-- Username -->
            <div class="mb-3">
              <label for="username" class="form-label">
                <i class="fa-solid fa-at me-1"></i>
                Username
              </label>
              <input 
                type="text" 
                class="form-control"
                :class="{ 'is-invalid': error && !form.username }"
                id="username" 
                v-model="form.username"
                placeholder="tu_usuario"
                required
              />
            </div>

            <!-- Email -->
            <div class="mb-3">
              <label for="email" class="form-label">
                <i class="fa-solid fa-envelope me-1"></i>
                Email
              </label>
              <input 
                type="email" 
                class="form-control"
                :class="{ 'is-invalid': error && !form.email }"
                id="email" 
                v-model="form.email"
                placeholder="tu_email@ejemplo.com"
                required
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
                  :class="{ 'is-invalid': error && !form.password }"
                  id="password" 
                  v-model="form.password"
                  placeholder="Mínimo 6 caracteres"
                  required
                />
                <button 
                  type="button" 
                  class="btn-toggle-password"
                  @click="showPassword = !showPassword"
                  tabindex="-1"
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

            <!-- Success Message -->
            <div v-if="exito" class="alert alert-success mt-3 mb-3">
              <i class="fa-solid fa-check-circle me-2"></i>
              <span>{{ exito }}</span>
            </div>

            <!-- Submit Button -->
            <button 
              type="submit" 
              class="btn btn-danger btn-register w-100"
              :disabled="cargando || !form.nombre || !form.apellido || !form.username || !form.email || !form.password"
            >
              <span v-if="cargando" class="spinner-border spinner-border-sm me-2"></span>
              <span>{{ cargando ? 'Creando cuenta...' : 'Crear Cuenta' }}</span>
            </button>

            <!-- Login Link -->
            <div class="text-center mt-4">
              <small class="text-muted">
                ¿Ya tienes cuenta? 
                <router-link to="/login" class="text-primary fw-bold text-decoration-none">
                  Inicia sesión aquí
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
import { ref, reactive } from 'vue'
import { registerUser } from '@/services/api'

// Formulario reactivo
const form = reactive({
  nombre: '',
  apellido: '',
  username: '',
  email: '',
  password: ''
})

const showPassword = ref(false)
const cargando = ref(false)
const error = ref(null)
const exito = ref(null)

const handleRegister = async () => {
  // Validación básica
  if (!form.nombre?.trim()) {
    error.value = 'El nombre es requerido'
    return
  }
  if (!form.apellido?.trim()) {
    error.value = 'El apellido es requerido'
    return
  }
  if (!form.username?.trim()) {
    error.value = 'El username es requerido'
    return
  }
  if (!form.email?.trim()) {
    error.value = 'El email es requerido'
    return
  }
  if (!form.password?.trim() || form.password.length < 6) {
    error.value = 'La contraseña debe tener al menos 6 caracteres'
    return
  }

  cargando.value = true
  error.value = null
  exito.value = null

  try {
    const data = await registerUser(form)
    
    exito.value = `¡Bienvenido, ${data.nombre}! Tu cuenta ha sido creada. Ya puedes iniciar sesión.`
    
    // Limpiar formulario
    Object.keys(form).forEach(key => form[key] = '')
    showPassword.value = false

  } catch (err) {
    error.value = err.message || 'Error al crear la cuenta'
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
  --success-color: #28a745;
  --dark-bg: #1a1a1a;
  --card-bg: #2b2b2b;
  --border-color: #444;
  --text-muted: #999;
}

/* ===================== CONTENEDOR PRINCIPAL ===================== */

.register-page {
  width: 100%;
  height: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 1rem;
}

.register-content {
  width: 100%;
  max-width: 500px;
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
  margin-bottom: 1.5rem;
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

/* ===================== ROW & COLS ===================== */

.row {
  display: flex;
  flex-wrap: wrap;
  margin: -0.375rem;
}

.col-12 {
  flex: 0 0 100%;
  padding: 0.375rem;
}

.col-md-6 {
  flex: 0 0 100%;
}

@media (min-width: 768px) {
  .col-md-6 {
    flex: 0 0 50%;
  }
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

/* ===================== ALERTS ===================== */

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

.alert-success {
  background-color: rgba(40, 167, 69, 0.1);
  border: 1px solid var(--success-color);
  color: #28a745;
}

.alert i {
  font-size: 1.1rem;
  flex-shrink: 0;
}

/* ===================== BOTÓN PRINCIPAL ===================== */

.btn-register {
  background-color: var(--danger-color) !important;
  border-color: var(--danger-color) !important;
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

.btn-register:hover:not(:disabled) {
  background-color: #e60a0a !important;
  border-color: #e60a0a !important;
  box-shadow: 0 4px 12px rgba(255, 43, 43, 0.3);
  transform: translateY(-2px);
}

.btn-register:active:not(:disabled) {
  transform: translateY(0);
}

.btn-register:disabled {
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
  .register-page {
    padding: 1rem;
  }

  .register-content {
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

  .btn-register {
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

  .row {
    margin: 0;
  }

  .col-12,
  .col-md-6 {
    padding: 0;
  }

  .col-md-6 {
    flex: 0 0 100%;
  }
}

/* TABLET (576px - 768px) */
@media (min-width: 576px) and (max-width: 768px) {
  .card-body {
    padding: 2rem 1.75rem;
  }

  .register-content {
    max-width: 480px;
  }

  .col-md-6 {
    flex: 0 0 50%;
  }
}

/* DESKTOP (768px+) */
@media (min-width: 768px) {
  .register-content {
    max-width: 500px;
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

  .col-md-6 {
    flex: 0 0 50%;
  }
}

/* LARGE SCREENS (1024px+) */
@media (min-width: 1024px) {
  .register-content {
    max-width: 550px;
  }

  .card {
    box-shadow: 0 12px 48px rgba(0, 0, 0, 0.8);
  }

  .btn-register:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 8px 24px rgba(255, 43, 43, 0.4);
  }
}

/* EXTRA LARGE (1440px+) */
@media (min-width: 1440px) {
  .register-content {
    max-width: 600px;
  }
}
</style>
