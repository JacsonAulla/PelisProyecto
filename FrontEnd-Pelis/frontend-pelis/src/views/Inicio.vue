<template>
  <div class="inicio-page">
    <main class="inicio-main">
      <!-- HEADER SECTION -->
      <div class="section-header">
        <i class="fa-solid fa-film fa-lg text-primary"></i>
        <h1 class="section-title">Catálogo de Películas</h1>
      </div>

      <!-- LOADING STATE -->
      <div v-if="cargando" class="loading-container">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="text-muted mt-2">Cargando películas...</p>
      </div>

      <!-- ERROR STATE -->
      <div v-else-if="error" class="alert alert-danger alert-dismissible fade show">
        <i class="fa-solid fa-exclamation-circle me-2"></i>
        <strong>Error:</strong> {{ error }}
      </div>

      <!-- SUCCESS ALERTS -->
      <div v-if="compraExito" class="alert alert-success alert-dismissible fade show">
        <i class="fa-solid fa-check-circle me-2"></i>
        {{ compraExito }}
      </div>

      <div v-if="compraError" class="alert alert-danger alert-dismissible fade show">
        <i class="fa-solid fa-exclamation-circle me-2"></i>
        <strong>Error:</strong> {{ compraError }}
      </div>

      <!-- MOVIES GRID -->
      <div v-if="!cargando && !error">
        <div v-if="peliculas.length" class="movies-grid">
          <div 
            v-for="pelicula in peliculas" 
            :key="pelicula.id"
            class="movie-card"
          >
            <!-- Movie Poster -->
            <router-link :to="`/pelicula/${pelicula.id}`" class="poster-link">
              <div class="poster-container">
                <img
                  :src="getPoster(pelicula)"
                  :alt="pelicula.titulo"
                  class="poster-image"
                  loading="lazy"
                />
                <div class="poster-overlay">
                  <button class="play-btn">
                    <i class="fa-solid fa-play"></i>
                  </button>
                </div>
              </div>
            </router-link>

            <!-- Movie Info -->
            <div class="movie-info">
              <h3 class="movie-title">
                <router-link :to="`/pelicula/${pelicula.id}`" class="title-link">
                  {{ pelicula.titulo }}
                </router-link>
              </h3>

              <div class="movie-details">
                <span class="year">{{ pelicula.anioLanzamiento }}</span>
                <span class="duration">{{ pelicula.duracionMinutos }}m</span>
              </div>

              <div class="movie-price">
                S/ {{ pelicula.precioComprar.toFixed(2) }}
              </div>

              <!-- Buy Button -->
              <button 
                v-if="authStore.isLoggedIn"
                @click="handleCompra(pelicula)"
                :disabled="compraCargando"
                class="btn btn-comprar"
              >
                <i class="fa-solid fa-cart-plus me-1"></i>
                <span>{{ compraCargando ? 'Comprando...' : 'Comprar' }}</span>
              </button>

              <!-- Login Prompt -->
              <div v-else class="login-prompt">
                <small class="text-muted">
                  <router-link to="/login" class="text-primary">Inicia sesión</router-link>
                  para comprar
                </small>
              </div>
            </div>
          </div>
        </div>

        <!-- No Movies -->
        <div v-else class="empty-state">
          <i class="fa-solid fa-film empty-icon"></i>
          <p class="text-muted">No hay películas disponibles en este momento.</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPublicPeliculas, createOrden } from '@/services/api'
import { useAuthStore } from '@/stores/authStore'

// ========== STATE ==========

const peliculas = ref([])
const cargando = ref(true)
const error = ref(null)

const compraExito = ref(null)
const compraError = ref(null)
const compraCargando = ref(false)

const authStore = useAuthStore()

// ========== LIFECYCLE ==========

onMounted(async () => {
  try {
    const data = await getPublicPeliculas()
    peliculas.value = data
  } catch (e) {
    error.value = e.message || 'Error al cargar las películas'
  } finally {
    cargando.value = false
  }
})

// ========== METHODS ==========

/**
 * Obtiene la URL del poster de una película
 * Intenta múltiples campos para compatibilidad
 */
function getPoster(pelicula) {
  if (!pelicula?.imgFrente) {
    return '/peliculas/placeholder.jpg'
  }
  return `/peliculas/${pelicula.imgFrente}`  // ← /peliculas/inception-vertical.jpg
}

/**
 * Maneja la compra de una película
 */
async function handleCompra(pelicula) {
  compraExito.value = null
  compraError.value = null
  compraCargando.value = true

  try {
    const ordenCreada = await createOrden([pelicula.id])
    
    compraExito.value = `¡"${pelicula.titulo}" añadida a tu biblioteca!`
    
    // Limpiar alerta después de 5 segundos
    setTimeout(() => {
      compraExito.value = null
    }, 5000)

  } catch (err) {
    compraError.value = err.message || 'Error al comprar la película'
    
    // Limpiar alerta después de 5 segundos
    setTimeout(() => {
      compraError.value = null
    }, 5000)
  } finally {
    compraCargando.value = false
  }
}
</script>

<style scoped>
/* ===================== VARIABLES ===================== */

:root {
  --primary-color: #0b75ff;
  --danger-color: #ff2b2b;
  --dark-bg: #1a1a1a;
  --card-bg: #2b2b2b;
  --border-color: #444;
  --text-light: rgba(255, 255, 255, 0.9);
  --text-muted: rgba(255, 255, 255, 0.6);
}

/* ===================== PAGE LAYOUT ===================== */

.inicio-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 2rem 1rem;
}

.inicio-main {
  max-width: 1400px;
  margin: 0 auto;
}

/* ===================== HEADER ===================== */

.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 2rem;
  padding: 1rem 0;
  border-bottom: 2px solid var(--border-color);
}

.section-header i {
  color: var(--primary-color);
  font-size: 1.5rem;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  letter-spacing: 0.5px;
}

/* ===================== LOADING STATE ===================== */

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  min-height: 300px;
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}

/* ===================== ALERTS ===================== */

.alert {
  display: flex;
  align-items: center;
  padding: 1rem;
  margin-bottom: 1.5rem;
  border-radius: 8px;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.alert-danger {
  background-color: rgba(255, 43, 43, 0.1);
  border: 1px solid var(--danger-color);
  color: #ff6b6b;
}

.alert-success {
  background-color: rgba(40, 167, 69, 0.1);
  border: 1px solid #28a745;
  color: #28a745;
}

.alert i {
  flex-shrink: 0;
  font-size: 1.2rem;
}

/* ===================== MOVIES GRID ===================== */

.movies-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

/* ===================== MOVIE CARD ===================== */

.movie-card {
  display: flex;
  flex-direction: column;
  background: var(--card-bg);
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  height: 100%;
}

.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(11, 117, 255, 0.2);
}

/* ===================== POSTER ===================== */

.poster-link {
  display: block;
  text-decoration: none;
  overflow: hidden;
  position: relative;
}

.poster-container {
  position: relative;
  aspect-ratio: 2 / 3;
  overflow: hidden;
  background: linear-gradient(135deg, #1a1a1a, #2b2b2b);
}

.poster-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.movie-card:hover .poster-image {
  transform: scale(1.05);
}

.poster-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.movie-card:hover .poster-overlay {
  opacity: 1;
}

.play-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--primary-color);
  border: none;
  color: #fff;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.4);
}

.play-btn:hover {
  background: #0a5fd8;
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(11, 117, 255, 0.6);
}

/* ===================== MOVIE INFO ===================== */

.movie-info {
  display: flex;
  flex-direction: column;
  padding: 1rem;
  flex: 1;
}

.movie-title {
  font-size: 0.95rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.title-link {
  color: #fff;
  text-decoration: none;
  transition: color 0.2s;
}

.title-link:hover {
  color: var(--primary-color);
}

.movie-details {
  display: flex;
  gap: 0.75rem;
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
}

.year,
.duration {
  padding: 0.25rem 0.5rem;
  background: rgba(11, 117, 255, 0.1);
  border-radius: 4px;
}

.movie-price {
  font-size: 1rem;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 1rem;
}

/* ===================== BUTTONS ===================== */

.btn-comprar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.6rem 1rem;
  background: var(--primary-color);
  border: none;
  color: #fff;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  margin-top: auto;
}

.btn-comprar:hover:not(:disabled) {
  background: #0a5fd8;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
  transform: translateY(-2px);
}

.btn-comprar:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-prompt {
  display: flex;
  justify-content: center;
  padding: 0.5rem 0;
  margin-top: auto;
}

.text-primary {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  transition: opacity 0.2s;
}

.text-primary:hover {
  opacity: 0.8;
  text-decoration: underline;
}

/* ===================== EMPTY STATE ===================== */

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  min-height: 300px;
}

.empty-icon {
  font-size: 4rem;
  color: var(--text-muted);
  margin-bottom: 1rem;
  opacity: 0.5;
}

/* ===================== RESPONSIVE ===================== */

/* TABLET (768px and down) */
@media (max-width: 768px) {
  .inicio-page {
    padding: 1.5rem 1rem;
  }

  .section-header {
    margin-bottom: 1.5rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .movies-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 1rem;
  }

  .movie-info {
    padding: 0.75rem;
  }

  .movie-title {
    font-size: 0.85rem;
  }

  .movie-details {
    font-size: 0.75rem;
  }

  .movie-price {
    font-size: 0.9rem;
  }

  .btn-comprar {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }
}

/* MOBILE (576px and down) */
@media (max-width: 576px) {
  .inicio-page {
    padding: 1rem 0.75rem;
  }

  .section-header {
    margin-bottom: 1rem;
    border-bottom: 1px solid var(--border-color);
    padding: 0.75rem 0;
  }

  .section-header i {
    font-size: 1.25rem;
  }

  .section-title {
    font-size: 1.25rem;
  }

  .movies-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 0.75rem;
  }

  .movie-card {
    border-radius: 6px;
  }

  .movie-info {
    padding: 0.5rem;
  }

  .movie-title {
    font-size: 0.8rem;
    margin-bottom: 0.25rem;
  }

  .movie-details {
    font-size: 0.7rem;
    margin-bottom: 0.25rem;
  }

  .movie-price {
    font-size: 0.85rem;
    margin-bottom: 0.5rem;
  }

  .btn-comprar {
    padding: 0.45rem 0.6rem;
    font-size: 0.75rem;
  }

  .play-btn {
    width: 48px;
    height: 48px;
    font-size: 1.2rem;
  }

  .alert {
    font-size: 0.9rem;
    padding: 0.75rem;
    margin-bottom: 1rem;
  }
}

/* EXTRA SMALL (320px and down) */
@media (max-width: 320px) {
  .section-title {
    font-size: 1.1rem;
  }

  .movies-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }

  .movie-title {
    font-size: 0.75rem;
  }
}
</style>
