<template>
  <div class="pelicula-page">
    <main class="pelicula-main">
      <!-- LOADING STATE -->
      <div v-if="cargando" class="loading-container">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="text-muted mt-2">Cargando película...</p>
      </div>

      <!-- ERROR STATE -->
      <div v-else-if="error" class="alert alert-danger alert-dismissible fade show">
        <i class="fa-solid fa-exclamation-circle me-2"></i>
        <strong>Error:</strong> {{ error }}
      </div>

      <!-- PELÍCULA DETALLE -->
      <div v-else-if="pelicula" class="pelicula-detalle">
        <!-- HERO SECTION -->
        <div class="hero-section">
          <div class="hero-background">
            <img 
              :src="getPoster(pelicula)" 
              :alt="pelicula.titulo"
              class="hero-image"
            />
            <div class="hero-overlay"></div>
          </div>

          <div class="hero-content">
            <h1 class="pelicula-titulo">{{ pelicula.titulo }}</h1>
            <div class="pelicula-metadata">
              <span class="meta-item">
                <i class="fa-solid fa-calendar"></i>
                {{ pelicula.anioLanzamiento }}
              </span>
              <span class="meta-item">
                <i class="fa-solid fa-clock"></i>
                {{ pelicula.duracionMinutos }} min
              </span>
              <span v-if="pelicula.generos?.length" class="meta-item">
                <i class="fa-solid fa-tag"></i>
                {{ pelicula.generos.map(g => g.nombre).join(', ') }}
              </span>
            </div>
          </div>
        </div>

        <!-- CONTENIDO PRINCIPAL -->
        <div class="contenido-principal">
          <div class="row g-4">
            <!-- POSTER + COMPRA -->
            <div class="col-lg-4">
              <div class="poster-section">
                <div class="poster-container">
                  <img
                    :src="getPoster(pelicula)"
                    :alt="pelicula.titulo"
                    class="poster-image"
                  />
                </div>

                <!-- PRECIO Y BOTÓN -->
                <div class="compra-section">
                  <div class="precio-box">
                    <label class="precio-label">Precio</label>
                    <div class="precio-amount">
                      S/ {{ pelicula.precioComprar.toFixed(2) }}
                    </div>
                  </div>

                  <!-- BOTÓN COMPRAR -->
                  <button 
                    v-if="authStore.isLoggedIn"
                    @click="handleCompra"
                    :disabled="compraCargando"
                    class="btn-comprar-principal"
                  >
                    <span v-if="compraCargando">
                      <i class="fa-solid fa-spinner fa-spin me-2"></i>
                      Procesando...
                    </span>
                    <span v-else>
                      <i class="fa-solid fa-cart-plus me-2"></i>
                      Comprar Ahora
                    </span>
                  </button>

                  <!-- LOGIN PROMPT -->
                  <router-link 
                    v-else
                    to="/login" 
                    class="btn-login-prompt"
                  >
                    <i class="fa-solid fa-sign-in-alt me-2"></i>
                    Inicia sesión para comprar
                  </router-link>

                  <!-- ALERTS -->
                  <div v-if="compraExito" class="alert alert-success mt-3">
                    <i class="fa-solid fa-check-circle me-2"></i>
                    {{ compraExito }}
                  </div>
                  <div v-if="compraError" class="alert alert-danger mt-3">
                    <i class="fa-solid fa-exclamation-circle me-2"></i>
                    {{ compraError }}
                  </div>
                </div>
              </div>
            </div>

            <!-- INFORMACIÓN -->
            <div class="col-lg-8">
              <div class="info-section">
                <!-- DESCRIPCIÓN -->
                <div class="info-block">
                  <h2 class="info-title">Sinopsis</h2>
                  <p class="info-text">{{ pelicula.descripcion }}</p>
                </div>

                <!-- GÉNEROS -->
                <div v-if="pelicula.generos?.length" class="info-block">
                  <h2 class="info-title">Géneros</h2>
                  <div class="generos-list">
                    <span 
                      v-for="genero in pelicula.generos" 
                      :key="genero.id"
                      class="genero-badge"
                    >
                      {{ genero.nombre }}
                    </span>
                  </div>
                </div>

                <!-- DETALLES TÉCNICOS -->
                <div class="info-block">
                  <h2 class="info-title">Detalles</h2>
                  <div class="details-grid">
                    <div class="detail-row">
                      <label>Año de Lanzamiento</label>
                      <span>{{ pelicula.anioLanzamiento }}</span>
                    </div>
                    <div class="detail-row">
                      <label>Duración</label>
                      <span>{{ pelicula.duracionMinutos }} minutos</span>
                    </div>
                    <div class="detail-row">
                      <label>Disponibilidad</label>
                      <span v-if="pelicula.disponible" class="badge-disponible">
                        <i class="fa-solid fa-check-circle me-1"></i>
                        Disponible
                      </span>
                      <span v-else class="badge-no-disponible">
                        <i class="fa-solid fa-times-circle me-1"></i>
                        No disponible
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- NO ENCONTRADO -->
      <div v-else class="empty-state">
        <i class="fa-solid fa-film empty-icon"></i>
        <h2>Película no encontrada</h2>
        <router-link to="/" class="btn btn-primary btn-back">
          <i class="fa-solid fa-arrow-left me-2"></i>
          Volver al catálogo
        </router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPeliculaDetalle, createOrden } from '@/services/api'
import { useAuthStore } from '@/stores/authStore'

// ========== STATE ==========

const pelicula = ref(null)
const cargando = ref(true)
const error = ref(null)

const compraCargando = ref(false)
const compraError = ref(null)
const compraExito = ref(null)

const route = useRoute()
const authStore = useAuthStore()

// ========== LIFECYCLE ==========

onMounted(async () => {
  try {
    const peliculaId = route.params.id
    pelicula.value = await getPeliculaDetalle(peliculaId)
  } catch (err) {
    error.value = err.message || 'Error al cargar la película'
  } finally {
    cargando.value = false
  }
})

// ========== METHODS ==========

/**
 * Obtiene la URL del poster
 */
function getPoster(p) {
  if (!p?.imgFrente) {
    return '/peliculas/placeholder.jpg'
  }
  return `/peliculas/${p.imgFrente}`
}

/**
 * Maneja la compra de la película
 */
async function handleCompra() {
  compraExito.value = null
  compraError.value = null
  compraCargando.value = true

  try {
    const ordenCreada = await createOrden([pelicula.value.id])
    compraExito.value = `¡"${pelicula.value.titulo}" ha sido añadida a tu biblioteca!`
    
    // Limpiar alerta después de 5 segundos
    setTimeout(() => {
      compraExito.value = null
    }, 5000)
  } catch (err) {
    compraError.value = err.message || 'Error al comprar la película'
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
  --success-color: #28a745;
  --dark-bg: #1a1a1a;
  --card-bg: #2b2b2b;
  --border-color: #444;
  --text-light: rgba(255, 255, 255, 0.9);
  --text-muted: rgba(255, 255, 255, 0.6);
}

/* ===================== PAGE LAYOUT ===================== */

.pelicula-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 2rem 1rem;
}

.pelicula-main {
  max-width: 1400px;
  margin: 0 auto;
}

/* ===================== LOADING & EMPTY STATES ===================== */

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  min-height: 400px;
}

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

.empty-state h2 {
  font-size: 1.8rem;
  color: #fff;
  margin-bottom: 1rem;
}

.btn-back {
  background: var(--primary-color);
  border: none;
  color: #fff;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #0a5fd8;
  transform: translateY(-2px);
}

/* ===================== ALERTS ===================== */

.alert {
  display: flex;
  align-items: center;
  padding: 1rem;
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
  border: 1px solid var(--success-color);
  color: #52d74f;
}

.alert i {
  flex-shrink: 0;
}

/* ===================== HERO SECTION ===================== */

.pelicula-detalle {
  color: #fff;
}

.hero-section {
  position: relative;
  margin: -2rem -1rem 0 -1rem;
  padding: 2rem 1rem;
  min-height: 400px;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.4) blur(2px);
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.3),
    rgba(26, 26, 26, 0.9)
  );
}

.hero-content {
  position: relative;
  z-index: 2;
  width: 100%;
}

.pelicula-titulo {
  font-size: clamp(2rem, 6vw, 3.5rem);
  font-weight: 800;
  margin: 0;
  line-height: 1.1;
  letter-spacing: -1px;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.8);
}

.pelicula-metadata {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-top: 1.5rem;
  font-size: 1rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--text-muted);
}

.meta-item i {
  color: var(--primary-color);
}

/* ===================== CONTENIDO PRINCIPAL ===================== */

.contenido-principal {
  margin-top: 2rem;
}

/* ===================== POSTER SECTION ===================== */

.poster-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  position: sticky;
  top: 70px;
}

.poster-container {
  aspect-ratio: 2 / 3;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.5);
}

.poster-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ===================== COMPRA SECTION ===================== */

.compra-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.precio-box {
  background: rgba(11, 117, 255, 0.1);
  border: 2px solid var(--primary-color);
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
}

.precio-label {
  display: block;
  font-size: 0.9rem;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
}

.precio-amount {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--primary-color);
}

.btn-comprar-principal {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  background: var(--primary-color);
  border: none;
  color: #fff;
  border-radius: 8px;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-comprar-principal:hover:not(:disabled) {
  background: #0a5fd8;
  box-shadow: 0 6px 20px rgba(11, 117, 255, 0.4);
  transform: translateY(-3px);
}

.btn-comprar-principal:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-login-prompt {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  background: var(--primary-color);
  border: none;
  color: #fff;
  border-radius: 8px;
  font-weight: 700;
  font-size: 1rem;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn-login-prompt:hover {
  background: #0a5fd8;
  box-shadow: 0 6px 20px rgba(11, 117, 255, 0.4);
  transform: translateY(-3px);
}

/* ===================== INFO SECTION ===================== */

.info-section {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.info-block {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 1.5rem;
}

.info-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0 0 1rem 0;
  color: #fff;
}

.info-text {
  color: var(--text-light);
  line-height: 1.6;
  margin: 0;
}

/* GÉNEROS */

.generos-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.genero-badge {
  display: inline-block;
  background: rgba(11, 117, 255, 0.2);
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

/* DETAILS GRID */

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.detail-row label {
  font-size: 0.9rem;
  color: var(--text-muted);
  font-weight: 600;
}

.detail-row span {
  font-size: 1rem;
  color: var(--text-light);
  font-weight: 600;
}

.badge-disponible {
  color: var(--success-color);
}

.badge-no-disponible {
  color: var(--danger-color);
}

/* ===================== RESPONSIVE ===================== */

@media (max-width: 992px) {
  .poster-section {
    position: static;
  }

  .pelicula-titulo {
    font-size: clamp(1.5rem, 5vw, 2.5rem);
  }

  .hero-section {
    min-height: 300px;
  }
}

@media (max-width: 768px) {
  .pelicula-page {
    padding: 1.5rem 0.75rem;
  }

  .hero-section {
    margin: -1.5rem -0.75rem 0 -0.75rem;
    padding: 1.5rem 0.75rem;
    min-height: 250px;
  }

  .pelicula-titulo {
    font-size: 1.75rem;
  }

  .pelicula-metadata {
    gap: 1rem;
    font-size: 0.9rem;
  }

  .contenido-principal {
    margin-top: 1.5rem;
  }

  .info-block {
    padding: 1rem;
  }

  .info-title {
    font-size: 1.25rem;
  }

  .details-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}

@media (max-width: 576px) {
  .pelicula-page {
    padding: 1rem 0.5rem;
  }

  .hero-section {
    margin: -1rem -0.5rem 0 -0.5rem;
    padding: 1rem 0.5rem;
    min-height: 200px;
  }

  .pelicula-titulo {
    font-size: 1.4rem;
  }

  .pelicula-metadata {
    gap: 0.75rem;
    font-size: 0.8rem;
  }

  .contenido-principal {
    margin-top: 1rem;
  }

  .poster-section {
    gap: 1rem;
  }

  .precio-box {
    padding: 1rem;
  }

  .precio-amount {
    font-size: 2rem;
  }

  .btn-comprar-principal,
  .btn-login-prompt {
    padding: 0.75rem 1rem;
    font-size: 0.9rem;
  }

  .info-block {
    padding: 0.75rem;
  }

  .info-title {
    font-size: 1.1rem;
  }

  .info-text {
    font-size: 0.95rem;
  }

  .genero-badge {
    font-size: 0.8rem;
    padding: 0.4rem 0.75rem;
  }

  .detail-row label,
  .detail-row span {
    font-size: 0.9rem;
  }
}
</style>
