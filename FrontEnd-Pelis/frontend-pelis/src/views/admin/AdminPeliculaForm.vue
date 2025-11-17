<template>
  <div class="pelicula-form-page">
    <!-- HEADER -->
    <div class="header-section">
      <h1 class="text-white">
        <i :class="isEditMode ? 'fa-solid fa-film me-2' : 'fa-solid fa-plus me-2'"></i>
        {{ isEditMode ? 'Editar Película' : 'Crear Nueva Película' }}
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
            
            <!-- TÍTULO Y DISPONIBILIDAD -->
            <div class="row">
              <div class="col-md-9 mb-3">
                <label for="titulo" class="form-label">
                  <i class="fa-solid fa-heading me-1"></i>
                  Título
                </label>
                <input 
                  type="text" 
                  class="form-control form-control-custom" 
                  id="titulo" 
                  v-model="form.titulo" 
                  placeholder="Ej: La Matriz"
                  required
                >
              </div>
              <div class="col-md-3 mb-3">
                <label for="disponible" class="form-label">
                  <i class="fa-solid fa-check-circle me-1"></i>
                  Estado
                </label>
                <select 
                  class="form-select form-select-custom" 
                  id="disponible" 
                  v-model="form.disponible"
                >
                  <option :value="true">✓ Disponible</option>
                  <option :value="false">✗ No Disponible</option>
                </select>
              </div>
            </div>

            <!-- DESCRIPCIÓN -->
            <div class="mb-3">
              <label for="descripcion" class="form-label">
                <i class="fa-solid fa-align-left me-1"></i>
                Descripción
              </label>
              <textarea 
                class="form-control form-control-custom" 
                id="descripcion" 
                rows="3" 
                v-model="form.descripcion" 
                placeholder="Describe la película aquí..."
                required
              ></textarea>
            </div>

            <!-- AÑO, DURACIÓN Y PRECIO -->
            <div class="row">
              <div class="col-md-4 mb-3">
                <label for="anioLanzamiento" class="form-label">
                  <i class="fa-solid fa-calendar me-1"></i>
                  Año de Lanzamiento
                </label>
                <input 
                  type="number" 
                  class="form-control form-control-custom" 
                  id="anioLanzamiento" 
                  v-model.number="form.anioLanzamiento"
                  placeholder="Ej: 1999"
                  required
                >
              </div>
              <div class="col-md-4 mb-3">
                <label for="duracionMinutos" class="form-label">
                  <i class="fa-solid fa-hourglass-end me-1"></i>
                  Duración (minutos)
                </label>
                <input 
                  type="number" 
                  class="form-control form-control-custom" 
                  id="duracionMinutos" 
                  v-model.number="form.duracionMinutos"
                  placeholder="Ej: 136"
                  required
                >
              </div>
              <div class="col-md-4 mb-3">
                <label for="precioComprar" class="form-label">
                  <i class="fa-solid fa-dollar-sign me-1"></i>
                  Precio (S/)
                </label>
                <input 
                  type="number" 
                  step="0.01" 
                  class="form-control form-control-custom" 
                  id="precioComprar" 
                  v-model.number="form.precioComprar"
                  placeholder="Ej: 29.99"
                  required
                >
              </div>
            </div>

            <!-- IMÁGENES Y URLs -->
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="imgFrente" class="form-label">
                  <i class="fa-solid fa-image me-1"></i>
                  URL Imagen de Frente (Poster)
                </label>
                <input 
                  type="text" 
                  class="form-control form-control-custom" 
                  id="imgFrente" 
                  v-model="form.imgFrente"
                  placeholder="Ej: matrix-vertical.jpg"
                >
              </div>
              <div class="col-md-6 mb-3">
                <label for="urlStream" class="form-label">
                  <i class="fa-solid fa-video me-1"></i>
                  URL del Stream (Video)
                </label>
                <input 
                  type="text" 
                  class="form-control form-control-custom" 
                  id="urlStream" 
                  v-model="form.urlStream"
                  placeholder="Ej: https://..."
                >
              </div>
            </div>

            <!-- GÉNEROS -->
            <div class="mb-4">
              <label for="generosId" class="form-label">
                <i class="fa-solid fa-tags me-1"></i>
                Géneros (Selección Múltiple)
              </label>
              <select 
                multiple 
                class="form-select form-select-custom select-multiple" 
                id="generosId" 
                v-model="form.generosId"
              >
                <option v-for="g in todosLosGeneros" :key="g.id" :value="g.id">
                  {{ g.nombre }}
                </option>
              </select>
              <small class="text-muted mt-2 d-block">
                <i class="fa-solid fa-info-circle me-1"></i>
                Mantén presionado CTRL (Windows) o CMD (Mac) para seleccionar múltiples géneros.
              </small>
            </div>

            <!-- BOTONES -->
            <div class="button-group">
              <router-link 
                :to="{ name: 'AdminPeliculas' }" 
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
                {{ isEditMode ? 'Guardar Cambios' : 'Crear Película' }}
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
import { 
  createMovie, 
  updateMovie, 
  getPeliculaDetalle, 
  getAllGenres 
} from '@/services/api'

// --- Setup de Ruteo ---
const route = useRoute()
const router = useRouter()
const isEditMode = computed(() => !!route.params.id)

// --- Estado de la Página ---
const loading = ref(true)
const error = ref(null)

// --- Datos de Géneros ---
const todosLosGeneros = ref([])

// --- Estado del Formulario ---
const submitLoading = ref(false)
const submitError = ref(null)
const submitSuccess = ref(null)

// --- Modelo del Formulario ---
const form = reactive({
  id: null,
  titulo: '',
  descripcion: '',
  anioLanzamiento: null,
  duracionMinutos: null,
  disponible: true,
  precioComprar: null,
  imgFrente: '',
  urlStream: '',
  generosId: []
})

// --- Carga Inicial ---
onMounted(async () => {
  try {
    // 1. Cargar todos los géneros
    todosLosGeneros.value = await getAllGenres()

    if (isEditMode.value) {
      // 2. Si estamos editando, cargar la película
      const peliculaId = route.params.id
      const movieData = await getPeliculaDetalle(peliculaId)
      
      // 3. Mapear datos al formulario
      form.id = movieData.id
      form.titulo = movieData.titulo
      form.descripcion = movieData.descripcion
      form.anioLanzamiento = movieData.anioLanzamiento
      form.duracionMinutos = movieData.duracionMinutos
      form.disponible = movieData.disponible
      form.precioComprar = movieData.precioComprar
      form.imgFrente = movieData.imgFrente
      form.urlStream = movieData.urlStream
      
      // Mapear géneros a lista de IDs
      form.generosId = movieData.generos.map(g => g.id)
    }
    
  } catch (e) {
    error.value = `Error al cargar: ${e.message}`
  } finally {
    loading.value = false
  }
})

// --- Envío del Formulario ---
const handleSubmit = async () => {
  submitLoading.value = true
  submitError.value = null
  submitSuccess.value = null
  
  try {
    let response

    const payload = {
      ...form,
      generosId: form.generosId
    }
    
    if (isEditMode.value) {
      response = await updateMovie(form.id, payload)
      submitSuccess.value = `✓ Película "${response.titulo}" actualizada con éxito.`
      
      setTimeout(() => {
        router.push({ name: 'AdminPeliculas' })
      }, 1500)
    } else {
      response = await createMovie(payload)
      submitSuccess.value = `✓ Película "${response.titulo}" creada con éxito. Redirigiendo...`
      
      setTimeout(() => {
        router.push({ name: 'AdminPeliculas' })
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

.pelicula-form-page {
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
  max-width: 1100px;
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
  flex-shrink: 0;
}

.form-control-custom,
.form-select-custom {
  background: #1a1a1a !important;
  border: 1px solid #444 !important;
  color: #fff !important;
  border-radius: 6px;
  transition: all 0.3s ease;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
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

/* TEXTAREA */
textarea.form-control-custom {
  resize: vertical;
  min-height: 100px;
}

/* SELECT MÚLTIPLE */
.select-multiple {
  height: 180px !important;
  padding: 0.5rem !important;
}

.select-multiple option {
  background: #2b2b2b;
  color: #fff;
  padding: 0.5rem;
  margin: 0.25rem 0;
}

.select-multiple option:checked {
  background: #0b75ff;
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

  .row {
    margin: 0;
  }

  .col-md-4,
  .col-md-6,
  .col-md-9 {
    margin-bottom: 1rem;
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

  .form-label {
    font-size: 0.9rem;
  }

  .form-control-custom,
  .form-select-custom {
    font-size: 1rem;
    padding: 0.65rem 0.75rem;
  }

  .select-multiple {
    height: 150px !important;
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

  small {
    font-size: 0.75rem !important;
  }
}
</style>
