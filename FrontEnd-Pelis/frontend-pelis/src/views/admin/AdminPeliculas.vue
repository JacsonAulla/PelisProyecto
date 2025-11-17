<template>
  <div class="peliculas-page">
    <!-- HEADER -->
    <div class="header-section">
      <h1 class="text-white">Gestionar Películas</h1>
      <router-link :to="{ name: 'AdminPeliculaNuevo' }" class="btn-crear-pelicula">
        <i class="fa-solid fa-plus me-2"></i> Crear Película
      </router-link>
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
        :to="{ name: 'AdminGeneros' }" 
        class="btn-nav-crud"
        :class="{ active: $route.name === 'AdminGeneros' }"
      >
        <i class="fa-solid fa-tag me-2"></i>
        Géneros
      </router-link>
    </div>

    <!-- LOADING -->
    <div v-if="cargando" class="text-center text-muted py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <!-- ERROR -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="fa-solid fa-exclamation-circle me-2"></i>
      {{ error }}
    </div>

    <!-- TABLE -->
    <div v-else class="card bg-dark border-secondary">
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-dark table-hover align-middle">
            <thead>
              <tr>
                <th scope="col">ID</th>
                <th scope="col" style="width: 25%;">Título</th>
                <th scope="col">Año</th>
                <th scope="col">Precio</th>
                <th scope="col">Estado</th>
                <th scope="col" class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in peliculas" :key="p.id">
                <th scope="row">{{ p.id }}</th>
                <td>{{ p.titulo }}</td>
                <td>{{ p.anioLanzamiento }}</td>
                <td>S/ {{ p.precioComprar.toFixed(2) }}</td>
                <td>
                  <span 
                    class="badge" 
                    :class="p.disponible ? 'text-bg-success' : 'text-bg-warning'"
                  >
                    {{ p.disponible ? 'Disponible' : 'No Disponible' }}
                  </span>
                </td>
                
                <td class="text-end">
                  <router-link 
                    :to="{ name: 'AdminPeliculaEditar', params: { id: p.id } }"
                    class="btn btn-sm btn-outline-primary me-2"
                  >
                    <i class="fa-solid fa-pencil"></i> Editar
                  </router-link>
                  
                  <button 
                    @click="handleDelete(p)" 
                    class="btn btn-sm btn-outline-danger"
                    :disabled="cargandoDelete === p.id"
                  >
                    <i v-if="cargandoDelete === p.id" class="fa-solid fa-spinner fa-spin"></i>
                    <i v-else class="fa-solid fa-trash"></i>
                    Eliminar
                  </button>
                </td>
              </tr>

              <tr v-if="peliculas.length === 0">
                <td colspan="6" class="text-center text-muted">
                  No se encontraron películas.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    
    <!-- PAGINACIÓN -->
    <nav v-if="totalPages > 1" class="pagination-section">
      <ul class="pagination">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <a class="page-link" @click="changePage(currentPage - 1)">
            <i class="fa-solid fa-chevron-left"></i>
          </a>
        </li>
        
        <li v-for="page in totalPages" :key="page" class="page-item" :class="{ active: currentPage === page - 1 }">
          <a class="page-link" @click="changePage(page - 1)">{{ page }}</a>
        </li>
        
        <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
          <a class="page-link" @click="changePage(currentPage + 1)">
            <i class="fa-solid fa-chevron-right"></i>
          </a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMoviesAdmin, deleteMovie } from '@/services/api'

// --- Estado de Datos ---
const peliculas = ref([])
const cargando = ref(true)
const cargandoDelete = ref(null)
const error = ref(null)

// --- Estado de Paginación ---
const currentPage = ref(0)
const totalPages = ref(0)
const pageSize = 10

// --- Lógica de Carga ---
onMounted(async () => {
  await cargarPeliculas()
})

async function cargarPeliculas() {
  cargando.value = true
  error.value = null
  try {
    const pageData = await getMoviesAdmin(currentPage.value, pageSize)
    peliculas.value = pageData.content
    totalPages.value = pageData.totalPages
  } catch (err) {
    error.value = err.message
  } finally {
    cargando.value = false
  }
}

async function changePage(newPage) {
  if (newPage >= 0 && newPage < totalPages.value) {
    currentPage.value = newPage
    await cargarPeliculas()
  }
}

const handleDelete = async (pelicula) => {
  if (!confirm(`¿Estás seguro de que quieres eliminar la película "${pelicula.titulo}"?`)) {
    return
  }
  
  cargandoDelete.value = pelicula.id
  try {
    await deleteMovie(pelicula.id)
    await cargarPeliculas()
  } catch (err) {
    error.value = `Error al eliminar: ${err.message}`
  } finally {
    cargandoDelete.value = null
  }
}
</script>

<style scoped>
/* ===================== CONTAINER ===================== */

.peliculas-page {
  width: 100%;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 0;
  margin: 0;
}

/* ===================== HEADER SECTION ===================== */

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 2rem;
  padding: 2rem 2rem 1rem 2rem;
  margin: 0;
}

.header-section h1 {
  margin: 0;
  font-size: 1.8rem;
}

/* ===================== BOTÓN CREAR ===================== */

.btn-crear-pelicula {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: #0b75ff;
  border: none;
  color: #fff;
  border-radius: 6px;
  font-weight: 700;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 0.95rem;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
}

.btn-crear-pelicula:hover {
  background: #0a5fd8;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(11, 117, 255, 0.4);
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

/* ===================== TABLE ===================== */

.card {
  background: #2b2b2b !important;
  border-color: #444 !important;
  margin: 0 2rem 2rem 2rem;
  border-radius: 12px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.card-body {
  padding: 0 !important;
}

.table-hover tbody tr:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.075);
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.table-dark {
  background-color: transparent !important;
  margin: 0 !important;
}

.table-dark th {
  color: rgba(255, 255, 255, 0.9);
  border-bottom: 2px solid #444;
  padding: 1rem;
  font-weight: 600;
}

.table-dark td {
  color: rgba(255, 255, 255, 0.9);
  border-color: #444;
  padding: 1rem;
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

/* ===================== PAGINACIÓN ===================== */

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 2rem;
  margin: 0;
}

.pagination {
  display: flex;
  gap: 0.5rem;
  list-style: none;
  margin: 0;
  padding: 0;
}

.page-item {
  display: flex;
}

.page-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  padding: 0;
  background: rgba(11, 117, 255, 0.15);
  border: 1px solid rgba(11, 117, 255, 0.3);
  color: #0b75ff;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  text-decoration: none;
}

.page-link:hover {
  background: rgba(11, 117, 255, 0.25);
  border-color: #0b75ff;
  color: #fff;
}

.page-item.active .page-link {
  background: #0b75ff;
  border-color: #0b75ff;
  color: #fff;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.4);
}

.page-item.disabled .page-link {
  opacity: 0.5;
  cursor: not-allowed;
  background: transparent;
}

/* ===================== RESPONSIVE ===================== */

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    padding: 1rem 1.5rem 0.5rem 1.5rem;
  }

  .btn-crear-pelicula {
    width: 100%;
    justify-content: center;
  }

  .nav-crudds {
    padding: 0 1.5rem 1rem 1.5rem;
  }

  .card {
    margin: 0 1.5rem 2rem 1.5rem;
  }

  .alert-danger {
    margin: 1rem 1.5rem;
  }

  .pagination-section {
    padding: 1rem;
  }

  .page-link {
    width: 36px;
    height: 36px;
    font-size: 0.85rem;
  }
}

@media (max-width: 576px) {
  .header-section {
    padding: 0.75rem 1rem 0.5rem 1rem;
  }

  .header-section h1 {
    font-size: 1.3rem;
  }

  .nav-crudds {
    padding: 0 1rem 0.75rem 1rem;
    gap: 0.5rem;
  }

  .btn-nav-crud {
    padding: 0.5rem 1rem;
    font-size: 0.8rem;
  }

  .card {
    margin: 0 1rem 1rem 1rem;
  }

  .alert-danger {
    margin: 0.75rem 1rem;
    font-size: 0.9rem;
  }

  .pagination {
    gap: 0.25rem;
  }

  .page-link {
    width: 32px;
    height: 32px;
    font-size: 0.75rem;
  }

  .table-dark th,
  .table-dark td {
    padding: 0.75rem 0.5rem;
    font-size: 0.85rem;
  }
}
</style>