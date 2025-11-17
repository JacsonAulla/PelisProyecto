<template>
  <div class="generos-page">
    <!-- HEADER -->
    <div class="header-section">
      <h1 class="text-white">Gestionar Géneros</h1>
    </div>

    <!-- BOTONES DE NAVEGACIÓN A OTROS CRUDDS -->
    <div class="nav-crudds">
      <router-link 
        :to="{ name: 'AdminUsuarios' }" 
        class="btn-nav-crud"
        :class="{ active: $route.name === 'AdminUsuarios' }"
      >
        <i class="fa-solid fa-users me-2"></i>
        Usuarios
      </router-link>
      
      <router-link 
        :to="{ name: 'AdminPeliculas' }" 
        class="btn-nav-crud"
        :class="{ active: $route.name === 'AdminPeliculas' }"
      >
        <i class="fa-solid fa-film me-2"></i>
        Películas
      </router-link>
    </div>

    <!-- ERROR GENERAL -->
    <div v-if="error" class="alert alert-danger">
      <i class="fa-solid fa-exclamation-circle me-2"></i>
      {{ error }}
    </div>

    <!-- CONTENIDO PRINCIPAL (2 COLUMNAS) -->
    <div class="content-section">
      <!-- FORMULARIO (Izquierda) -->
      <div class="form-section">
        <div class="card bg-dark border-secondary">
          <div class="card-header">
            <h5 class="text-white mb-0">
              <i :class="isEditing ? 'fa-solid fa-pen-to-square me-2' : 'fa-solid fa-plus me-2'"></i>
              {{ isEditing ? 'Editar Género' : 'Crear Nuevo Género' }}
            </h5>
          </div>
          <div class="card-body">
            <form @submit.prevent="handleSubmit">
              <div class="mb-3">
                <label for="nombre" class="form-label">Nombre del Género</label>
                <input 
                  type="text" 
                  class="form-control form-control-custom" 
                  id="nombre" 
                  v-model="genreForm.nombre" 
                  placeholder="Ej: Acción, Drama, Comedia..."
                  required
                >
              </div>

              <!-- ALERTS DEL FORMULARIO -->
              <div v-if="submitError" class="alert alert-warning py-2 mb-3">
                <i class="fa-solid fa-triangle-exclamation me-2"></i>
                {{ submitError }}
              </div>
              <div v-if="submitSuccess" class="alert alert-success py-2 mb-3">
                <i class="fa-solid fa-check-circle me-2"></i>
                {{ submitSuccess }}
              </div>

              <!-- BOTONES -->
              <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary flex-grow-1" :disabled="submitLoading">
                  <span v-if="submitLoading" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else :class="isEditing ? 'fa-solid fa-save me-2' : 'fa-solid fa-plus me-2'"></i>
                  {{ isEditing ? 'Guardar Cambios' : 'Crear Género' }}
                </button>
                <button 
                  v-if="isEditing" 
                  type="button" 
                  @click="resetForm" 
                  class="btn btn-outline-secondary"
                >
                  <i class="fa-solid fa-times me-1"></i>
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- TABLA (Derecha) -->
      <div class="table-section">
        <div v-if="cargando" class="loading-container">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
          <p class="text-muted mt-2">Cargando géneros...</p>
        </div>

        <div v-else class="card bg-dark border-secondary">
          <div class="card-header">
            <h5 class="text-white mb-0">
              <i class="fa-solid fa-list me-2"></i>
              Géneros ({{ generos.length }})
            </h5>
          </div>
          <div class="table-responsive">
            <table class="table table-dark table-hover align-middle tabla-generos">
              <thead>
                <tr>
                  <th style="width: 15%;">ID</th>
                  <th>Nombre</th>
                  <th style="width: 25%;" class="text-end">Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="generos.length === 0">
                  <td colspan="3" class="text-center text-muted py-4">
                    <i class="fa-solid fa-inbox fa-2x opacity-50 mb-2"></i>
                    <p>No hay géneros. ¡Crea el primero!</p>
                  </td>
                </tr>
                <tr v-for="genre in generos" :key="genre.id">
                  <th scope="row" class="id-cell">{{ genre.id }}</th>
                  <td class="nombre-cell">{{ genre.nombre }}</td>
                  <td class="acciones-cell">
                    <button 
                      @click="startEdit(genre)" 
                      class="btn btn-sm btn-outline-primary me-2"
                      title="Editar"
                    >
                      <i class="fa-solid fa-pencil"></i>
                      <span>Editar</span>
                    </button>
                    <button 
                      @click="handleDelete(genre.id)" 
                      class="btn btn-sm btn-outline-danger"
                      :disabled="cargandoDelete === genre.id"
                      title="Eliminar"
                    >
                      <i v-if="cargandoDelete === genre.id" class="fa-solid fa-spinner fa-spin"></i>
                      <i v-else class="fa-solid fa-trash"></i>
                      <span>Eliminar</span>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllGenres, createGenre, updateGenre, deleteGenre } from '@/services/api'

// --- Estado de Datos ---
const generos = ref([])
const cargando = ref(true)
const cargandoDelete = ref(null)
const error = ref(null)

// --- Estado del Formulario ---
const isEditing = ref(false)
const genreForm = reactive({ id: null, nombre: '' })
const submitLoading = ref(false)
const submitError = ref(null)
const submitSuccess = ref(null)

// --- Ciclo de Vida ---
onMounted(async () => {
  await cargarGeneros()
})

// --- Funciones CRUD ---

async function cargarGeneros() {
  cargando.value = true
  error.value = null
  try {
    generos.value = await getAllGenres()
  } catch (err) {
    error.value = err.message
  } finally {
    cargando.value = false
  }
}

function resetForm() {
  isEditing.value = false
  genreForm.id = null
  genreForm.nombre = ''
  submitError.value = null
  submitSuccess.value = null
}

async function handleSubmit() {
  submitLoading.value = true
  submitError.value = null
  submitSuccess.value = null
  
  try {
    let result
    const payload = { nombre: genreForm.nombre }

    if (isEditing.value) {
      result = await updateGenre(genreForm.id, payload)
      submitSuccess.value = `Género "${result.nombre}" actualizado.`
    } else {
      result = await createGenre(payload)
      submitSuccess.value = `Género "${result.nombre}" creado con éxito.`
    }
    
    resetForm()
    await cargarGeneros()
    
  } catch (err) {
    submitError.value = err.message
  } finally {
    submitLoading.value = false
  }
}

function startEdit(genre) {
  isEditing.value = true
  genreForm.id = genre.id
  genreForm.nombre = genre.nombre
  submitSuccess.value = null
}

async function handleDelete(id) {
  if (!confirm('¿Estás seguro de que quieres eliminar este género?')) return

  cargandoDelete.value = id
  try {
    await deleteGenre(id)
    await cargarGeneros()
  } catch (err) {
    error.value = `Error al eliminar: ${err.message}`
  } finally {
    cargandoDelete.value = null
  }
}
</script>

<style scoped>
/* ===================== CONTAINER ===================== */

.generos-page {
  width: 100%;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 0;
  margin: 0;
}

/* ===================== HEADER SECTION ===================== */

.header-section {
  padding: 2rem 2rem 1rem 2rem;
  margin: 0;
}

.header-section h1 {
  margin: 0;
  font-size: 1.8rem;
}

/* ===================== NAV CRUDS ===================== */

.nav-crudds {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  padding: 0 2rem 1.5rem 2rem;
  margin: 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-nav-crud {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.2rem;
  background: rgba(11, 117, 255, 0.15);
  border: 2px solid rgba(11, 117, 255, 0.3);
  color: #0b75ff;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.btn-nav-crud:hover {
  background: rgba(11, 117, 255, 0.25);
  border-color: #0b75ff;
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
}

.btn-nav-crud.active {
  background: #0b75ff;
  border-color: #0b75ff;
  color: #fff;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.4);
}

/* ===================== ALERT ===================== */

.alert-danger {
  background-color: rgba(255, 43, 43, 0.1) !important;
  border-color: #ff2b2b !important;
  color: #ff6b6b !important;
  display: flex;
  align-items: center;
  margin: 1rem 2rem;
  border-radius: 8px;
  padding: 1rem;
}

/* ===================== CONTENT SECTION ===================== */

.content-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  padding: 2rem 2rem;
  margin: 0;
}

/* ===================== FORM SECTION ===================== */

.form-section {
  min-height: 300px;
}

.card {
  background: #2b2b2b !important;
  border-color: #444 !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  height: 100%;
}

.card-header {
  background: rgba(11, 117, 255, 0.1) !important;
  border-bottom: 2px solid #444 !important;
  padding: 1.25rem !important;
}

.card-header h5 {
  font-weight: 700;
  color: #fff;
}

.card-header i {
  color: #0b75ff;
}

.card-body {
  padding: 1.5rem !important;
}

.form-label {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.form-control-custom {
  background: #1a1a1a !important;
  border: 1px solid #444 !important;
  color: #fff !important;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.form-control-custom:focus {
  background: #1a1a1a !important;
  border-color: #0b75ff !important;
  color: #fff !important;
  box-shadow: 0 0 0 0.2rem rgba(11, 117, 255, 0.25) !important;
}

.form-control-custom::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

/* ALERTS EN FORM */
.alert {
  display: flex;
  align-items: center;
  border-radius: 6px;
  font-size: 0.9rem;
}

.alert-warning {
  background-color: rgba(255, 193, 7, 0.1) !important;
  border: 1px solid rgba(255, 193, 7, 0.3) !important;
  color: #ffc107 !important;
}

.alert-success {
  background-color: rgba(40, 167, 69, 0.1) !important;
  border: 1px solid rgba(40, 167, 69, 0.3) !important;
  color: #52d74f !important;
}

/* BOTONES FORM */
.btn-primary {
  background: #0b75ff !important;
  border: none !important;
  color: #fff !important;
  font-weight: 700;
  transition: all 0.3s ease;
}

.btn-primary:hover:not(:disabled) {
  background: #0a5fd8 !important;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
  transform: translateY(-2px);
}

.btn-outline-secondary {
  color: rgba(255, 255, 255, 0.7) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
  transition: all 0.3s ease;
}

.btn-outline-secondary:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  color: #fff !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
}

/* ===================== TABLE SECTION ===================== */

.table-section {
  display: flex;
  flex-direction: column;
  min-height: 300px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.tabla-generos {
  margin: 0 !important;
}

.tabla-generos th {
  color: rgba(255, 255, 255, 0.9);
  border-bottom: 2px solid #444;
  padding: 1rem;
  font-weight: 600;
  font-size: 0.85rem;
  text-transform: uppercase;
}

.tabla-generos td {
  color: rgba(255, 255, 255, 0.9);
  border-color: #444;
  padding: 1rem;
}

.tabla-generos tbody tr:hover {
  background-color: rgba(11, 117, 255, 0.08);
}

.id-cell {
  text-align: center;
  color: #0b75ff;
  font-weight: 600;
}

.nombre-cell {
  font-weight: 500;
}

.acciones-cell {
  text-align: right;
}

.btn-outline-primary {
  color: #0b75ff !important;
  border-color: #0b75ff !important;
}

.btn-outline-primary:hover {
  background: #0b75ff !important;
  color: #fff !important;
}

.btn-outline-danger {
  color: #ff2b2b !important;
  border-color: #ff2b2b !important;
}

.btn-outline-danger:hover:not(:disabled) {
  background: #ff2b2b !important;
  color: #fff !important;
}

.btn-outline-danger:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-sm span {
  display: inline;
}

/* ===================== RESPONSIVE ===================== */

@media (max-width: 1024px) {
  .content-section {
    grid-template-columns: 1fr;
    gap: 1.5rem;
    padding: 1.5rem;
  }
}

@media (max-width: 768px) {
  .header-section {
    padding: 1rem 1.5rem 0.5rem 1.5rem;
  }

  .header-section h1 {
    font-size: 1.4rem;
  }

  .nav-crudds {
    padding: 0 1.5rem 1rem 1.5rem;
  }

  .alert-danger {
    margin: 1rem 1.5rem;
  }

  .content-section {
    grid-template-columns: 1fr;
    gap: 1.5rem;
    padding: 1.5rem;
  }

  .btn-sm span {
    display: none;
  }

  .btn-sm {
    padding: 0.4rem 0.6rem !important;
  }
}

@media (max-width: 576px) {
  .header-section {
    padding: 0.75rem 1rem 0.5rem 1rem;
  }

  .header-section h1 {
    font-size: 1.2rem;
  }

  .nav-crudds {
    padding: 0 1rem 0.75rem 1rem;
    gap: 0.5rem;
  }

  .alert-danger {
    margin: 0.75rem 1rem;
    font-size: 0.85rem;
  }

  .content-section {
    padding: 1rem;
  }

  .card-body {
    padding: 1rem !important;
  }

  .tabla-generos th,
  .tabla-generos td {
    padding: 0.75rem 0.5rem;
    font-size: 0.85rem;
  }
}
</style>
