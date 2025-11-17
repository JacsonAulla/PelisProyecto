<template>
  <div class="usuarios-page">
    <!-- HEADER -->
    <div class="header-section">
      <h1 class="text-white">Gestionar Usuarios</h1>
      
      <router-link :to="{ name: 'AdminUsuarioNuevo' }" class="btn-crear-usuario">
        <i class="fa-solid fa-plus me-2"></i> Crear Usuario
      </router-link>
    </div>

    <!-- BOTONES DE NAVEGACIÓN A OTROS CRUDDS -->
    <div class="nav-crudds">
      <router-link 
        :to="{ name: 'AdminPeliculas' }" 
        class="btn-nav-crud"
        :class="{ active: $route.name === 'AdminPeliculas' }"
      >
        <i class="fa-solid fa-film me-2"></i>
        Películas
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
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
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
                <th scope="col">Nombre</th>
                <th scope="col">Username</th>
                <th scope="col">Email</th>
                <th scope="col">Rol</th>
                <th scope="col" class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in usuarios" :key="user.id">
                <th scope="row">{{ user.id }}</th>
                <td>{{ user.nombre }} {{ user.apellido }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <span 
                    class="badge" 
                    :class="user.rol === 'ADMIN' ? 'text-bg-danger' : 'text-bg-secondary'"
                  >
                    {{ user.rol }}
                  </span>
                </td>
                
                <td class="text-end">
                  <router-link 
                    :to="{ name: 'AdminUsuarioEditar', params: { id: user.id } }" 
                    class="btn btn-sm btn-outline-primary me-2"
                  >
                    <i class="fa-solid fa-pencil"></i> Editar
                  </router-link>

                  <button 
                    @click="handleDelete(user)" 
                    class="btn btn-sm btn-outline-danger"
                    :disabled="user.rol === 'ADMIN'"
                  >
                    <i class="fa-solid fa-trash"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllUsers, deleteUser } from '@/services/api'

// --- Estado ---
const usuarios = ref([])
const cargando = ref(true)
const error = ref(null)

// --- Carga Inicial ---
onMounted(async () => {
  await cargarUsuarios()
})

// --- Funciones de API ---
async function cargarUsuarios() {
  cargando.value = true
  error.value = null
  try {
    usuarios.value = await getAllUsers()
  } catch (err) {
    error.value = err.message
  } finally {
    cargando.value = false
  }
}

const handleDelete = async (usuario) => {
  if (!confirm(`¿Estás seguro de que quieres eliminar a "${usuario.username}"?`)) {
    return
  }

  if (usuario.rol === 'ADMIN') {
    alert("No se pueden eliminar administradores desde aquí.")
    return
  }
  
  try {
    await deleteUser(usuario.id)
    await cargarUsuarios()
  } catch (err) {
    error.value = `Error al eliminar: ${err.message}`
  }
}
</script>

<style scoped>
/* ===================== CONTAINER ===================== */

.usuarios-page {
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

.btn-crear-usuario {
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

.btn-crear-usuario:hover {
  background: #0a5fd8;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(11, 117, 255, 0.4);
}

.btn-crear-usuario:active {
  transform: translateY(0);
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
}

.table-dark th {
  color: rgba(255, 255, 255, 0.9);
  border-bottom: 2px solid #444;
}

.table-dark td {
  color: rgba(255, 255, 255, 0.9);
  border-color: #444;
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
}

/* ===================== RESPONSIVE ===================== */

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    padding: 1rem 1.5rem 0.5rem 1.5rem;
  }

  .btn-crear-usuario {
    width: 100%;
    justify-content: center;
  }

  .nav-crudds {
    padding: 0 1.5rem 1rem 1.5rem;
  }

  .card {
    margin: 0 1.5rem 1.5rem 1.5rem;
  }

  .alert-danger {
    margin: 1rem 1.5rem;
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
}
</style>