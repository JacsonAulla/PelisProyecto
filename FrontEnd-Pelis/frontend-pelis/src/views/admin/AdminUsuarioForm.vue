<template>
  <div class="usuario-form-page">
    <!-- HEADER -->
    <div class="header-section">
      <h1 class="text-white">
        <i :class="isEditMode ? 'fa-solid fa-pen-to-square me-2' : 'fa-solid fa-user-plus me-2'"></i>
        {{ isEditMode ? 'Editar Usuario' : 'Crear Nuevo Usuario' }}
      </h1>
    </div>

    <!-- LOADING INITIAL -->
    <div v-if="loading" class="loading-container">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="text-muted mt-2">Cargando datos...</p>
    </div>

    <!-- ERROR INICIAL -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="fa-solid fa-exclamation-circle me-2"></i>
      {{ error }}
    </div>

    <!-- FORM -->
    <div v-else class="form-container">
      <div class="card bg-dark border-secondary">
        <div class="card-body p-4">
          
          <!-- ALERTS DEL FORMULARIO -->
          <div v-if="submitSuccess" class="alert alert-success mb-4">
            <i class="fa-solid fa-check-circle me-2"></i>
            {{ submitSuccess }}
          </div>
          <div v-if="submitError" class="alert alert-danger mb-4">
            <i class="fa-solid fa-exclamation-circle me-2"></i>
            {{ submitError }}
          </div>

          <!-- FORMULARIO -->
          <form @submit.prevent="handleSubmit">
            
            <!-- NOMBRE Y APELLIDO -->
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="nombre" class="form-label">
                  <i class="fa-solid fa-user me-1"></i>
                  Nombre
                </label>
                <input 
                  type="text" 
                  class="form-control form-control-custom" 
                  id="nombre" 
                  v-model="form.nombre" 
                  placeholder="Ej: Juan"
                  required
                >
              </div>
              <div class="col-md-6 mb-3">
                <label for="apellido" class="form-label">
                  <i class="fa-solid fa-user me-1"></i>
                  Apellido
                </label>
                <input 
                  type="text" 
                  class="form-control form-control-custom" 
                  id="apellido" 
                  v-model="form.apellido" 
                  placeholder="Ej: Pérez"
                  required
                >
              </div>
            </div>

            <!-- USERNAME Y EMAIL -->
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="username" class="form-label">
                  <i class="fa-solid fa-at me-1"></i>
                  Username
                </label>
                <input 
                  type="text" 
                  class="form-control form-control-custom" 
                  id="username" 
                  v-model="form.username" 
                  placeholder="Ej: juan.perez"
                  required
                >
              </div>
              <div class="col-md-6 mb-3">
                <label for="email" class="form-label">
                  <i class="fa-solid fa-envelope me-1"></i>
                  Email
                </label>
                <input 
                  type="email" 
                  class="form-control form-control-custom" 
                  id="email" 
                  v-model="form.email" 
                  placeholder="Ej: juan@example.com"
                  required
                >
              </div>
            </div>

            <!-- ROL Y CONTRASEÑA (solo en crear) -->
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="rol" class="form-label">
                  <i class="fa-solid fa-shield me-1"></i>
                  Rol
                </label>
                <select 
                  class="form-select form-select-custom" 
                  id="rol" 
                  v-model="form.rol" 
                  required
                >
                  <option value="USUARIO">👤 Usuario Normal</option>
                  <option value="ADMIN">🛡️ Administrador</option>
                </select>
              </div>
              <div v-if="!isEditMode" class="col-md-6 mb-3">
                <label for="password" class="form-label">
                  <i class="fa-solid fa-lock me-1"></i>
                  Contraseña
                </label>
                <input 
                  type="password" 
                  class="form-control form-control-custom" 
                  id="password" 
                  v-model="form.password" 
                  placeholder="Mínimo 6 caracteres"
                  required
                >
              </div>
            </div>

            <!-- BOTONES -->
            <div class="button-group">
              <router-link 
                :to="{ name: 'AdminUsuarios' }" 
                class="btn btn-outline-secondary"
              >
                <i class="fa-solid fa-times me-2"></i>
                Cancelar
              </router-link>
              <button 
                type="submit" 
                class="btn btn-primary" 
                :disabled="submitLoading"
              >
                <span v-if="submitLoading" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else :class="isEditMode ? 'fa-solid fa-save me-2' : 'fa-solid fa-plus me-2'"></i>
                {{ isEditMode ? 'Guardar Cambios' : 'Crear Usuario' }}
              </button>
            </div>
          </form>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createUser, updateUser, getUserById } from '@/services/api'

// --- Setup Ruteo ---
const route = useRoute()
const router = useRouter()
const isEditMode = computed(() => !!route.params.id)

// --- Estado de la Página ---
const loading = ref(isEditMode.value)
const error = ref(null)

// --- Estado del Formulario ---
const submitLoading = ref(false)
const submitError = ref(null)
const submitSuccess = ref(null)

// --- Modelo del Formulario ---
const form = reactive({
  id: null,
  nombre: '',
  apellido: '',
  username: '',
  email: '',
  password: '',
  rol: 'USUARIO'
})

// --- Carga Inicial (para Editar) ---
onMounted(async () => {
  if (isEditMode.value) {
    try {
      const userId = route.params.id
      const userData = await getUserById(userId)
      
      form.id = userData.id
      form.nombre = userData.nombre
      form.apellido = userData.apellido
      form.username = userData.username
      form.email = userData.email
      form.rol = userData.rol
      
    } catch (e) {
      error.value = `Error al cargar el usuario: ${e.message}`
    } finally {
      loading.value = false
    }
  }
})

// --- Envío del Formulario ---
const handleSubmit = async () => {
  submitLoading.value = true
  submitError.value = null
  submitSuccess.value = null
  
  try {
    let response

    const payload = isEditMode.value ? 
      { 
        nombre: form.nombre, 
        apellido: form.apellido, 
        username: form.username, 
        email: form.email, 
        rol: form.rol 
      } : 
      { ...form }

    if (isEditMode.value) {
      response = await updateUser(form.id, payload)
      submitSuccess.value = `✓ Usuario ${response.username} actualizado con éxito.`
      
      setTimeout(() => {
        router.push({ name: 'AdminUsuarios' })
      }, 1500)
    } else {
      response = await createUser(payload)
      submitSuccess.value = `✓ Usuario ${response.username} creado con éxito. Redirigiendo...`
      
      setTimeout(() => {
        router.push({ name: 'AdminUsuarios' })
      }, 1500)
    }

  } catch (e) {
    submitError.value = e.message || 'Error desconocido al procesar el formulario.'
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
/* ===================== CONTAINER ===================== */

.usuario-form-page {
  width: 100%;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 0;
  margin: 0;
  min-height: 100vh;
}

/* ===================== HEADER ===================== */

.header-section {
  padding: 2rem 2rem 1rem 2rem;
}

.header-section h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  color: #fff;
}

.header-section i {
  color: #0b75ff;
}

/* ===================== LOADING ===================== */

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  min-height: 400px;
}

/* ===================== ALERTS ===================== */

.alert {
  display: flex;
  align-items: center;
  border-radius: 8px;
  padding: 1rem;
}

.alert-danger {
  background-color: rgba(255, 43, 43, 0.1);
  border: 1px solid #ff2b2b;
  color: #ff6b6b;
}

.alert-success {
  background-color: rgba(40, 167, 69, 0.1);
  border: 1px solid #28a745;
  color: #52d74f;
}

.alert i {
  flex-shrink: 0;
}

/* ===================== FORM CONTAINER ===================== */

.form-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 2rem;
}

.card {
  background: #2b2b2b !important;
  border-color: #444 !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.card-body {
  background: #2b2b2b;
}

/* ===================== FORM LABELS Y INPUTS ===================== */

.form-label {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 600;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
}

.form-label i {
  color: #0b75ff;
  margin-right: 0.5rem;
}

.form-control-custom,
.form-select-custom {
  background: #1a1a1a !important;
  border: 1px solid #444 !important;
  color: #fff !important;
  border-radius: 6px;
  transition: all 0.3s ease;
  padding: 0.75rem 1rem;
}

.form-control-custom:focus,
.form-select-custom:focus {
  background: #1a1a1a !important;
  border-color: #0b75ff !important;
  color: #fff !important;
  box-shadow: 0 0 0 0.2rem rgba(11, 117, 255, 0.25) !important;
}

.form-control-custom::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.form-select-custom {
  cursor: pointer;
}

.form-select-custom option {
  background: #2b2b2b;
  color: #fff;
}

/* ===================== BOTONES ===================== */

.button-group {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
  border-radius: 6px;
  transition: all 0.3s ease;
  text-decoration: none;
  font-size: 0.95rem;
}

.btn-primary {
  background: #0b75ff !important;
  border: none !important;
  color: #fff !important;
}

.btn-primary:hover:not(:disabled) {
  background: #0a5fd8 !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-outline-secondary {
  background: transparent !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: rgba(255, 255, 255, 0.7) !important;
}

.btn-outline-secondary:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
  color: #fff !important;
}

/* ===================== RESPONSIVE ===================== */

@media (max-width: 768px) {
  .header-section {
    padding: 1rem 1.5rem 0.5rem 1.5rem;
  }

  .header-section h1 {
    font-size: 1.4rem;
  }

  .form-container {
    padding: 1.5rem;
  }

  .button-group {
    flex-direction: column-reverse;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .header-section {
    padding: 0.75rem 1rem 0.5rem 1rem;
  }

  .header-section h1 {
    font-size: 1.2rem;
  }

  .form-container {
    padding: 1rem;
  }

  .card-body {
    padding: 1.5rem !important;
  }

  .row {
    margin: 0;
  }

  .col-md-6 {
    margin-bottom: 1rem;
  }

  .form-label {
    font-size: 0.9rem;
  }

  .form-control-custom,
  .form-select-custom {
    font-size: 1rem;
  }

  .button-group {
    gap: 0.5rem;
    margin-top: 1.5rem;
    padding-top: 1.5rem;
  }

  .btn {
    padding: 0.65rem 1rem;
    font-size: 0.85rem;
  }

  .btn span {
    display: none;
  }

  .btn i {
    margin: 0;
  }
}
</style>
