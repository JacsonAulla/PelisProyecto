<template>
  <div class="biblioteca-page">
    <main class="biblioteca-main">
      <!-- HEADER SECTION -->
      <div class="section-header">
        <i class="fa-solid fa-book fa-lg text-primary"></i>
        <h1 class="section-title">Mi Biblioteca</h1>
      </div>

      <!-- LOADING STATE -->
      <div v-if="cargando" class="loading-container">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="text-muted mt-2">Cargando tu biblioteca...</p>
      </div>

      <!-- ERROR STATE -->
      <div v-else-if="error" class="alert alert-danger alert-dismissible fade show">
        <i class="fa-solid fa-exclamation-circle me-2"></i>
        <strong>Error:</strong> {{ error }}
      </div>

      <!-- EMPTY STATE -->
      <div v-else-if="items.length === 0" class="empty-state">
        <i class="fa-solid fa-inbox empty-icon"></i>
        <h2 class="empty-title">Tu biblioteca está vacía</h2>
        <p class="text-muted">Compra películas para verlas aquí</p>
        <router-link to="/" class="btn btn-primary btn-browse">
          <i class="fa-solid fa-search me-2"></i>
          Explorar Películas
        </router-link>
      </div>

      <!-- BIBLIOTECA GRID -->
      <div v-else>
        <div class="biblioteca-stats">
          <span class="stat-item">
            <i class="fa-solid fa-film me-2"></i>
            <strong>{{ items.length }}</strong> películas
          </span>
        </div>

        <div class="movies-grid">
          <div 
            v-for="item in items" 
            :key="item.idBiblioteca"
            class="movie-card"
          >
            <!-- Poster -->
            <router-link :to="`/pelicula/${item.pelicula.id}`" class="poster-link">
              <div class="poster-container">
                <img
                  :src="getPoster(item.pelicula)"
                  :alt="item.pelicula.titulo"
                  class="poster-image"
                  loading="lazy"
                />
                <div class="poster-overlay">
                  <button class="play-btn" @click.prevent="handlePlay(item.pelicula)">
                    <i class="fa-solid fa-play"></i>
                  </button>
                </div>
              </div>
            </router-link>

            <!-- Movie Info -->
            <div class="movie-info">
              <h3 class="movie-title">
                <router-link :to="`/pelicula/${item.pelicula.id}`" class="title-link">
                  {{ item.pelicula.titulo }}
                </router-link>
              </h3>

              <div class="movie-meta">
                <span class="year">{{ item.pelicula.anioLanzamiento }}</span>
                <span class="duration">{{ item.pelicula.duracionMinutos }}m</span>
              </div>

              <div class="purchase-info">
                <small class="text-muted">
                  <i class="fa-solid fa-calendar-check me-1"></i>
                  Comprada: {{ formatDate(item.fechaCompra) }}
                </small>
              </div>

              <!-- Play Button -->
              <button 
                @click="handlePlay(item.pelicula)"
                class="btn btn-play"
              >
                <i class="fa-solid fa-play me-1"></i>
                <span>Ver Ahora</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMiBiblioteca } from '@/services/api'

// ========== ROUTER ==========
const router = useRouter()

// ========== STATE ==========
const items = ref([])
const cargando = ref(true)
const error = ref(null)

// ========== LIFECYCLE ==========
onMounted(async () => {
  try {
    const pageData = await getMiBiblioteca()
    items.value = pageData.content || []
  } catch (err) {
    error.value = err.message || 'Error al cargar tu biblioteca'
  } finally {
    cargando.value = false
  }
})

// ========== METHODS ==========

/**
 * Obtiene la URL del poster
 */
function getPoster(pelicula) {
  if (!pelicula?.imgFrente) {
    return '/peliculas/placeholder.jpg'
  }
  
  if (pelicula.imgFrente.startsWith('http')) {
    return pelicula.imgFrente
  }
  
  return `/peliculas/${pelicula.imgFrente}`
}

/**
 * Formatea la fecha de compra
 */
function formatDate(dateString) {
  try {
    return new Date(dateString).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    })
  } catch {
    return 'Fecha desconocida'
  }
}

/**
 * Navega a la página de reproducción
 */
function handlePlay(pelicula) {
  router.push({
    name: 'PeliculaReproducir',
    params: { id: pelicula.id }
  })
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

.biblioteca-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 2rem 1rem;
}

.biblioteca-main {
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

/* ===================== EMPTY STATE ===================== */

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  min-height: 400px;
}

.empty-icon {
  font-size: 5rem;
  color: var(--text-muted);
  margin-bottom: 1.5rem;
  opacity: 0.5;
}

.empty-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #fff;
  margin-bottom: 0.5rem;
}

.btn-browse {
  background: var(--primary-color);
  border: none;
  color: #fff;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  margin-top: 1.5rem;
  transition: all 0.3s ease;
}

.btn-browse:hover {
  background: #0a5fd8;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
  transform: translateY(-2px);
}

/* ===================== STATS ===================== */

.biblioteca-stats {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background: rgba(11, 117, 255, 0.1);
  border-radius: 8px;
  border-left: 4px solid var(--primary-color);
}

.stat-item {
  display: flex;
  align-items: center;
  color: var(--text-light);
  font-size: 0.95rem;
}

.stat-item i {
  color: var(--primary-color);
}

.stat-item strong {
  color: var(--primary-color);
  margin: 0 0.25rem;
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

.movie-meta {
  display: flex;
  gap: 0.75rem;
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-bottom: 0.75rem;
}

.year,
.duration {
  padding: 0.25rem 0.5rem;
  background: rgba(11, 117, 255, 0.1);
  border-radius: 4px;
}

.purchase-info {
  margin-bottom: 1rem;
  font-size: 0.85rem;
}

.purchase-info i {
  color: var(--primary-color);
}

/* ===================== PLAY BUTTON ===================== */

.btn-play {
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

.btn-play:hover {
  background: #0a5fd8;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
  transform: translateY(-2px);
}

.btn-play:active {
  transform: translateY(0);
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

.alert i {
  flex-shrink: 0;
  font-size: 1.2rem;
}

/* ===================== RESPONSIVE ===================== */

/* TABLET (768px and down) */
@media (max-width: 768px) {
  .biblioteca-page {
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

  .movie-meta {
    font-size: 0.75rem;
  }

  .purchase-info {
    font-size: 0.8rem;
  }

  .btn-play {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }

  .biblioteca-stats {
    padding: 0.75rem;
  }

  .stat-item {
    font-size: 0.85rem;
  }
}

/* MOBILE (576px and down) */
@media (max-width: 576px) {
  .biblioteca-page {
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

  .movie-meta {
    font-size: 0.7rem;
    margin-bottom: 0.5rem;
  }

  .purchase-info {
    font-size: 0.75rem;
    margin-bottom: 0.5rem;
  }

  .btn-play {
    padding: 0.45rem 0.6rem;
    font-size: 0.75rem;
  }

  .play-btn {
    width: 48px;
    height: 48px;
    font-size: 1.2rem;
  }

  .empty-state {
    padding: 2rem 1rem;
    min-height: 300px;
  }

  .empty-icon {
    font-size: 3.5rem;
  }

  .empty-title {
    font-size: 1.3rem;
  }

  .btn-browse {
    padding: 0.6rem 1.25rem;
    font-size: 0.9rem;
  }

  .biblioteca-stats {
    flex-wrap: wrap;
    padding: 0.75rem;
    gap: 1rem;
  }

  .stat-item {
    font-size: 0.8rem;
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
