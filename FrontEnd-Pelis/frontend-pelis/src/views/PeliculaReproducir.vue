<template>
  <div class="reproductor-page">
    <!-- HEADER -->
    <div class="header-reproductor">
      <router-link to="/" class="btn-volver">
        <i class="fa-solid fa-arrow-left me-2"></i>
        Volver
      </router-link>
      <h1 class="titulo-pelicula">{{ pelicula?.titulo }}</h1>
      <div style="width: 80px;"></div>
    </div>

    <!-- LOADING -->
    <div v-if="cargando" class="loading-full">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="text-muted mt-2">Preparando tu película...</p>
    </div>

    <!-- ERROR -->
    <div v-else-if="error" class="error-container">
      <div class="alert alert-danger">
        <i class="fa-solid fa-exclamation-circle me-2"></i>
        {{ error }}
      </div>
      <router-link to="/" class="btn btn-primary mt-3">
        Volver al inicio
      </router-link>
    </div>

    <!-- REPRODUCTOR -->
    <div v-else class="reproductor-container">
      <!-- VIDEO -->
      <video 
        ref="videoPlayer" 
        class="video-player"
        controls
      ></video>

      <!-- CONTROLES CUSTOM (Opcional) -->
      <div class="controles-custom">
        <div class="barra-progreso">
          <div 
            class="progreso-actual"
            :style="{ width: progressPercent + '%' }"
          ></div>
        </div>

        <div class="controles-bottom">
          <button @click="togglePlay" class="btn-control play-pause">
            <i :class="isPlaying ? 'fa-solid fa-pause' : 'fa-solid fa-play'"></i>
          </button>

          <div class="volumen-container">
            <button @click="toggleMute" class="btn-control">
              <i :class="isMuted ? 'fa-solid fa-volume-xmark' : 'fa-solid fa-volume-high'"></i>
            </button>
            <input 
              v-model.number="volume" 
              type="range" 
              min="0" 
              max="100" 
              class="slider-volumen"
              @change="changeVolume"
            >
          </div>

          <span class="tiempo">{{ currentTime }} / {{ duration }}</span>

          <div class="espaciador"></div>

          <button @click="toggleFullscreen" class="btn-control fullscreen">
            <i class="fa-solid fa-expand"></i>
          </button>
        </div>
      </div>

      <!-- INFO PELÍCULA -->
      <div class="info-pelicula-reproductor">
        <h2>{{ pelicula?.titulo }}</h2>
        <p class="year-duration">
          {{ pelicula?.anioLanzamiento }} • {{ pelicula?.duracionMinutos }} minutos
        </p>
        <p class="descripcion">{{ pelicula?.descripcion }}</p>

        <div class="metadata">
          <span v-if="pelicula?.generos?.length" class="generos">
            <strong>Géneros:</strong>
            {{ pelicula.generos.map(g => g.nombre).join(', ') }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { getPeliculaDetalle } from '@/services/api'
import HLS from 'hls.js'

// --- Setup ---
const route = useRoute()
const videoPlayer = ref(null)
const hlsInstance = ref(null)

// --- Estado ---
const pelicula = ref(null)
const cargando = ref(true)
const error = ref(null)

// --- Controles ---
const isPlaying = ref(false)
const isMuted = ref(false)
const volume = ref(100)
const currentTimeSeconds = ref(0)
const durationSeconds = ref(0)

// --- Computed ---
const progressPercent = computed(() => {
  if (durationSeconds.value === 0) return 0
  return (currentTimeSeconds.value / durationSeconds.value) * 100
})

const currentTime = computed(() => formatTime(currentTimeSeconds.value))
const duration = computed(() => formatTime(durationSeconds.value))

// --- Lifecycle ---
onMounted(async () => {
  try {
    // 1. Cargar película
    const peliculaId = route.params.id
    console.log('Cargando película ID:', peliculaId)
    pelicula.value = await getPeliculaDetalle(peliculaId)
    console.log('Película cargada:', pelicula.value)

    if (!pelicula.value?.urlStream) {
      error.value = 'Esta película no está disponible para reproducción.'
      return
    }

    // 2. Cambiar cargando a false PRIMERO
    cargando.value = false

    // 3. Esperar a que Vue renderice
    await nextTick()
    console.log('nextTick completado')

    // 4. Esperar más tiempo para asegurar que el DOM esté listo
    await new Promise(resolve => setTimeout(resolve, 500))
    console.log('Timeout completado')

    // 5. Verificar que videoPlayer está disponible
    console.log('videoPlayer.value:', videoPlayer.value)

    // 6. Inicializar HLS
    await initHLS()

    // 7. Setup eventos
    setupVideoEvents()

  } catch (err) {
    error.value = `Error al cargar: ${err.message}`
    console.error('Error completo:', err)
  } finally {
    cargando.value = false
  }
})

onUnmounted(() => {
  if (hlsInstance.value) {
    hlsInstance.value.destroy()
  }
  if (videoPlayer.value) {
    videoPlayer.value.pause()
  }
})

// --- Métodos HLS ---
const initHLS = async () => {
  const video = videoPlayer.value

  if (!video) {
    error.value = 'No se pudo acceder al video (ref es null)'
    console.error('videoPlayer es NULL')
    return
  }

  const url = pelicula.value.urlStream
  console.log('URL Stream:', url)

  // ← DETECTAR si es MP4 o M3U8
  if (url.endsWith('.mp4')) {
    // ✅ Es MP4: reproducir directamente
    console.log('✅ Detectado MP4, reproduciendo directamente')
    video.src = url
  } else if (url.endsWith('.m3u8')) {
    // ✅ Es M3U8: usar HLS.js
    console.log('✅ Detectado M3U8, usando HLS.js')
    
    if (HLS.isSupported()) {
      hlsInstance.value = new HLS({
        autoStartLoad: true,
        startLevel: -1,
        debug: false
      })

      hlsInstance.value.loadSource(url)
      hlsInstance.value.attachMedia(video)

      hlsInstance.value.on(HLS.Events.MANIFEST_PARSED, () => {
        console.log('✅ HLS manifest cargado')
      })

      hlsInstance.value.on(HLS.Events.ERROR, (event, data) => {
        console.error('HLS error:', data)
        if (data.fatal) {
          error.value = 'Error al reproducir el video'
        }
      })

    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      // Safari nativo
      video.src = url
    } else {
      error.value = 'Tu navegador no soporta esta reproducción'
    }
  } else {
    // ❓ Tipo desconocido, intentar reproducir directamente
    console.log('⚠️ Tipo desconocido, intentando reproducir directamente')
    video.src = url
  }
}

// --- Eventos Video ---
const setupVideoEvents = () => {
  const video = videoPlayer.value

  if (!video) {
    console.error('❌ Video element is NULL en setupVideoEvents')
    return
  }

  console.log('✅ Video element encontrado, agregando eventos')

  video.addEventListener('play', () => {
    isPlaying.value = true
  })

  video.addEventListener('pause', () => {
    isPlaying.value = false
  })

  video.addEventListener('timeupdate', () => {
    currentTimeSeconds.value = video.currentTime
  })

  video.addEventListener('loadedmetadata', () => {
    durationSeconds.value = video.duration
  })

  video.addEventListener('volumechange', () => {
    volume.value = video.volume * 100
    isMuted.value = video.muted
  })
}

// --- Controles ---
const togglePlay = () => {
  if (isPlaying.value) {
    videoPlayer.value.pause()
  } else {
    videoPlayer.value.play()
  }
}

const toggleMute = () => {
  videoPlayer.value.muted = !videoPlayer.value.muted
}

const changeVolume = () => {
  videoPlayer.value.volume = volume.value / 100
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    videoPlayer.value.requestFullscreen().catch(err => {
      console.error('Error fullscreen:', err)
    })
  } else {
    document.exitFullscreen()
  }
}

// --- Utilidades ---
const formatTime = (seconds) => {
  if (isNaN(seconds)) return '0:00'
  
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = Math.floor(seconds % 60)

  if (h > 0) {
    return `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  }
  return `${m}:${String(s).padStart(2, '0')}`
}
</script>


<style scoped>
/* ===================== VARIABLES ===================== */

:root {
  --primary-color: #0b75ff;
  --dark-bg: #1a1a1a;
  --card-bg: #2b2b2b;
  --border-color: #444;
  --text-light: rgba(255, 255, 255, 0.9);
  --text-muted: rgba(255, 255, 255, 0.6);
}

/* ===================== PAGE ===================== */

.reproductor-page {
  width: 100%;
  min-height: 100vh;
  background: var(--dark-bg);
  padding: 0;
  margin: 0;
}

/* ===================== HEADER ===================== */

.header-reproductor {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 2rem;
  background: rgba(0, 0, 0, 0.8);
  border-bottom: 1px solid var(--border-color);
  backdrop-filter: blur(10px);
  gap: 1rem;
}

.btn-volver {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.2rem;
  background: rgba(11, 117, 255, 0.2);
  border: 1px solid rgba(11, 117, 255, 0.3);
  color: #0b75ff;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-volver:hover {
  background: rgba(11, 117, 255, 0.3);
  border-color: #0b75ff;
}

.titulo-pelicula {
  flex: 1;
  text-align: center;
  font-size: 1.3rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ===================== LOADING ===================== */

.loading-full {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 500px;
  gap: 1rem;
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}

/* ===================== ERROR ===================== */

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 500px;
  padding: 2rem;
}

.alert {
  max-width: 500px;
  padding: 1rem;
  background-color: rgba(255, 43, 43, 0.1);
  border: 1px solid #ff2b2b;
  color: #ff6b6b;
  border-radius: 8px;
  display: flex;
  align-items: center;
}

/* ===================== REPRODUCTOR ===================== */

.reproductor-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.video-player {
  width: 100%;
  max-width: 100%;
  height: auto;
  aspect-ratio: 16 / 9;
  background: #000;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  margin-bottom: 2rem;
}

/* ===================== CONTROLES CUSTOM ===================== */

.controles-custom {
  background: rgba(11, 117, 255, 0.1);
  border: 1px solid rgba(11, 117, 255, 0.2);
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 2rem;
}

.barra-progreso {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  cursor: pointer;
  margin-bottom: 1rem;
  overflow: hidden;
}

.progreso-actual {
  height: 100%;
  background: linear-gradient(90deg, #0b75ff, #0a5fd8);
  border-radius: 3px;
  transition: width 0.1s linear;
}

.controles-bottom {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.btn-control {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: rgba(11, 117, 255, 0.2);
  border: 1px solid rgba(11, 117, 255, 0.3);
  color: #0b75ff;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1.1rem;
}

.btn-control:hover {
  background: rgba(11, 117, 255, 0.3);
  border-color: #0b75ff;
}

.play-pause {
  width: 50px;
  height: 50px;
  font-size: 1.3rem;
  background: #0b75ff;
  border: none;
  color: #fff;
}

.play-pause:hover {
  background: #0a5fd8;
}

.volumen-container {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.slider-volumen {
  width: 80px;
  height: 4px;
  -webkit-appearance: none;
  appearance: none;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  outline: none;
  cursor: pointer;
}

.slider-volumen::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 14px;
  height: 14px;
  background: #0b75ff;
  border-radius: 50%;
  cursor: pointer;
}

.slider-volumen::-moz-range-thumb {
  width: 14px;
  height: 14px;
  background: #0b75ff;
  border-radius: 50%;
  cursor: pointer;
  border: none;
}

.tiempo {
  color: var(--text-light);
  font-weight: 600;
  min-width: 80px;
  text-align: right;
}

.espaciador {
  flex: 1;
}

.fullscreen {
  font-size: 1rem;
}

/* ===================== INFO PELÍCULA ===================== */

.info-pelicula-reproductor {
  background: var(--card-bg);
  padding: 2rem;
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.info-pelicula-reproductor h2 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #fff;
  margin: 0 0 0.5rem 0;
}

.year-duration {
  color: var(--text-muted);
  font-size: 1rem;
  margin: 0 0 1rem 0;
}

.descripcion {
  color: var(--text-light);
  line-height: 1.6;
  margin: 1rem 0;
  max-width: 800px;
}

.metadata {
  display: flex;
  gap: 2rem;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
}

.generos {
  color: var(--text-light);
  font-size: 0.95rem;
}

.generos strong {
  color: #0b75ff;
}

/* ===================== RESPONSIVE ===================== */

@media (max-width: 768px) {
  .header-reproductor {
    padding: 0.75rem 1.5rem;
    gap: 0.5rem;
  }

  .titulo-pelicula {
    font-size: 1rem;
  }

  .btn-volver {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }

  .reproductor-container {
    padding: 1.5rem 1rem;
  }

  .controles-custom {
    padding: 0.75rem;
  }

  .controles-bottom {
    gap: 0.75rem;
  }

  .btn-control {
    width: 36px;
    height: 36px;
    font-size: 1rem;
  }

  .play-pause {
    width: 44px;
    height: 44px;
  }

  .slider-volumen {
    width: 60px;
  }

  .tiempo {
    font-size: 0.85rem;
    min-width: 70px;
  }

  .info-pelicula-reproductor {
    padding: 1.5rem;
  }

  .info-pelicula-reproductor h2 {
    font-size: 1.5rem;
  }

  .metadata {
    flex-direction: column;
    gap: 1rem;
  }
}

@media (max-width: 576px) {
  .header-reproductor {
    padding: 0.5rem 1rem;
  }

  .titulo-pelicula {
    font-size: 0.9rem;
  }

  .btn-volver span {
    display: none;
  }

  .reproductor-container {
    padding: 1rem 0.75rem;
  }

  .controles-bottom {
    gap: 0.5rem;
    flex-wrap: wrap;
  }

  .btn-control {
    width: 32px;
    height: 32px;
    font-size: 0.9rem;
  }

  .play-pause {
    width: 40px;
    height: 40px;
    font-size: 1.1rem;
  }

  .slider-volumen {
    width: 50px;
  }

  .tiempo {
    flex-basis: 100%;
    text-align: left;
    font-size: 0.8rem;
    min-width: auto;
  }

  .espaciador {
    display: none;
  }

  .fullscreen {
    display: none;
  }

  .info-pelicula-reproductor {
    padding: 1rem;
  }

  .info-pelicula-reproductor h2 {
    font-size: 1.2rem;
  }

  .year-duration {
    font-size: 0.9rem;
  }

  .descripcion {
    font-size: 0.9rem;
  }
}
</style>
