<template>
  <div class="suscripcion-page">
    <main class="suscripcion-main">
      <!-- HEADER SECTION -->
      <div class="section-header">
        <i class="fa-solid fa-star fa-lg text-warning"></i>
        <h1 class="section-title">Mi Suscripción</h1>
      </div>

      <!-- LOADING STATE -->
      <div v-if="cargando" class="loading-container">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="text-muted mt-2">Cargando tu suscripción...</p>
      </div>

      <!-- ERROR STATE -->
      <div v-else-if="error" class="alert alert-danger alert-dismissible fade show">
        <i class="fa-solid fa-exclamation-circle me-2"></i>
        <strong>Error:</strong> {{ error }}
      </div>

      <!-- CONTENT -->
      <div v-else class="suscripcion-content">
        <!-- Alerts -->
        <div v-if="accionError" class="alert alert-danger alert-dismissible fade show">
          <i class="fa-solid fa-exclamation-circle me-2"></i>
          {{ accionError }}
        </div>
        <div v-if="accionExito" class="alert alert-success alert-dismissible fade show">
          <i class="fa-solid fa-check-circle me-2"></i>
          {{ accionExito }}
        </div>

        <!-- NO SUSCRIPTION -->
        <div v-if="!suscripcion" class="card card-no-subscription">
          <div class="card-body">
            <div class="no-subscription-content">
              <i class="fa-solid fa-inbox icon-empty"></i>
              <h2 class="title-no-subscription">No tienes un plan activo</h2>
              <p class="subtitle-no-subscription">
                Accede a todo nuestro catálogo sin límites
              </p>
              
              <div class="price-box">
                <div class="price-amount">S/ 9.99</div>
                <div class="price-period">por mes</div>
              </div>

              <ul class="benefits-list">
                <li>
                  <i class="fa-solid fa-check"></i>
                  Ver películas sin límites
                </li>
                <li>
                  <i class="fa-solid fa-check"></i>
                  Calidad HD 1080p
                </li>
                <li>
                  <i class="fa-solid fa-check"></i>
                  Ver en múltiples dispositivos
                </li>
                <li>
                  <i class="fa-solid fa-check"></i>
                  Cancelar en cualquier momento
                </li>
              </ul>

              <button 
                @click="handleSubscribe" 
                :disabled="accionCargando"
                class="btn btn-subscribe btn-lg"
              >
                <span v-if="accionCargando" class="spinner-border spinner-border-sm me-2"></span>
                <span>{{ accionCargando ? 'Procesando...' : 'Suscribirse Ahora' }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- ACTIVE SUBSCRIPTION -->
        <div v-else-if="suscripcion.estado === 'ACTIVA'" class="card card-active">
          <div class="card-body">
            <div class="subscription-status-active">
              <div class="status-header">
                <div class="status-badge status-active">
                  <i class="fa-solid fa-check-circle me-1"></i>
                  Activa
                </div>
              </div>

              <h2 class="title-active">Tu plan está activo</h2>

              <div class="subscription-details">
                <div class="detail-item">
                  <div class="detail-icon">
                    <i class="fa-solid fa-calendar-check"></i>
                  </div>
                  <div class="detail-content">
                    <label>Fecha de Inicio</label>
                    <p>{{ formatDate(suscripcion.fechaInicio) }}</p>
                  </div>
                </div>

                <div class="detail-item">
                  <div class="detail-icon">
                    <i class="fa-solid fa-calendar-days"></i>
                  </div>
                  <div class="detail-content">
                    <label>Próximo Cobro</label>
                    <p>{{ formatDate(suscripcion.fechaFin) }}</p>
                  </div>
                </div>

                <div class="detail-item">
                  <div class="detail-icon">
                    <i class="fa-solid fa-credit-card"></i>
                  </div>
                  <div class="detail-content">
                    <label>Precio Mensual</label>
                    <p class="price-highlight">S/ {{ suscripcion.precioPagado.toFixed(2) }}</p>
                  </div>
                </div>
              </div>

              <div class="subscription-note">
                <i class="fa-solid fa-info-circle me-2"></i>
                Se te cobrará automáticamente cada mes. Puedes cancelar en cualquier momento.
              </div>

              <button 
                @click="handleCancel" 
                :disabled="accionCargando"
                class="btn btn-cancel mt-4"
              >
                <span v-if="accionCargando" class="spinner-border spinner-border-sm me-2"></span>
                <span>{{ accionCargando ? 'Cancelando...' : 'Cancelar Suscripción' }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- CANCELLED/INACTIVE SUBSCRIPTION -->
        <div v-else class="card card-inactive">
          <div class="card-body">
            <div class="subscription-status-inactive">
              <div class="status-header">
                <div class="status-badge status-inactive">
                  <i class="fa-solid fa-circle-xmark me-1"></i>
                  {{ suscripcion.estado }}
                </div>
              </div>

              <h2 class="title-inactive">Tu plan está {{ suscripcion.estado }}</h2>
              
              <p class="subtitle-inactive">
                Tu acceso finalizó el {{ formatDate(suscripcion.fechaFin) }}
              </p>

              <div class="reactivate-note">
                <i class="fa-solid fa-info-circle me-2"></i>
                Vuelve a suscribirte para acceder a todo nuestro catálogo
              </div>

              <button 
                @click="handleSubscribe" 
                :disabled="accionCargando"
                class="btn btn-subscribe mt-4"
              >
                <span v-if="accionCargando" class="spinner-border spinner-border-sm me-2"></span>
                <span>{{ accionCargando ? 'Procesando...' : 'Volver a Suscribirse' }}</span>
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
import { 
  getMiSuscripcion, 
  createSuscripcion, 
  cancelSuscripcion 
} from '@/services/api'

// ========== STATE ==========

const suscripcion = ref(null)
const cargando = ref(true)
const error = ref(null)

const accionCargando = ref(false)
const accionError = ref(null)
const accionExito = ref(null)

// ========== LIFECYCLE ==========

onMounted(async () => {
  await cargarEstado()
  cargando.value = false
})

// ========== METHODS ==========

/**
 * Carga el estado actual de la suscripción
 */
async function cargarEstado() {
  try {
    suscripcion.value = await getMiSuscripcion()
  } catch (err) {
    error.value = err.message || 'Error al cargar tu suscripción'
  }
}

/**
 * Formatea fecha a formato legible
 */
function formatDate(dateString) {
  try {
    return new Date(dateString).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  } catch {
    return 'Fecha desconocida'
  }
}

/**
 * Maneja la suscripción
 */
async function handleSubscribe() {
  accionCargando.value = true
  accionError.value = null
  accionExito.value = null

  try {
    const nuevaSuscripcion = await createSuscripcion()
    suscripcion.value = nuevaSuscripcion
    accionExito.value = '¡Suscripción activada con éxito! Ahora tienes acceso completo.'
    
    // Limpiar alerta después de 5 segundos
    setTimeout(() => {
      accionExito.value = null
    }, 5000)
  } catch (err) {
    accionError.value = err.message || 'Error al crear la suscripción'
  } finally {
    accionCargando.value = false
  }
}

/**
 * Maneja la cancelación de suscripción
 */
async function handleCancel() {
  if (!confirm('¿Estás seguro? No podrás acceder al catálogo completo después de cancelar.')) {
    return
  }

  accionCargando.value = true
  accionError.value = null
  accionExito.value = null

  try {
    const suscripcionCancelada = await cancelSuscripcion()
    suscripcion.value = suscripcionCancelada
    accionExito.value = 'Tu suscripción ha sido cancelada. Te echaremos de menos.'
    
    // Limpiar alerta después de 5 segundos
    setTimeout(() => {
      accionExito.value = null
    }, 5000)
  } catch (err) {
    accionError.value = err.message || 'Error al cancelar la suscripción'
  } finally {
    accionCargando.value = false
  }
}
</script>

<style scoped>
/* ===================== VARIABLES ===================== */

:root {
  --primary-color: #0b75ff;
  --danger-color: #ff2b2b;
  --warning-color: #ffc107;
  --success-color: #28a745;
  --dark-bg: #1a1a1a;
  --card-bg: #2b2b2b;
  --border-color: #444;
  --text-light: rgba(255, 255, 255, 0.9);
  --text-muted: rgba(255, 255, 255, 0.6);
}

/* ===================== PAGE LAYOUT ===================== */

.suscripcion-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a1a 0%, #0f0f0f 100%);
  padding: 2rem 1rem;
}

.suscripcion-main {
  max-width: 900px;
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
  color: var(--warning-color);
  font-size: 1.5rem;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  letter-spacing: 0.5px;
}

/* ===================== LOADING & CONTENT ===================== */

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  min-height: 300px;
}

.suscripcion-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
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
  font-size: 1.2rem;
}

/* ===================== CARDS ===================== */

.card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.card-body {
  padding: 2rem;
}

/* ===================== NO SUBSCRIPTION ===================== */

.card-no-subscription {
  background: linear-gradient(135deg, #2b2b2b 0%, #1f1f1f 100%);
  border: 2px solid var(--primary-color);
}

.no-subscription-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.icon-empty {
  font-size: 4rem;
  color: var(--primary-color);
  margin-bottom: 1.5rem;
  opacity: 0.8;
}

.title-no-subscription {
  font-size: 1.8rem;
  font-weight: 700;
  color: #fff;
  margin-bottom: 0.5rem;
}

.subtitle-no-subscription {
  color: var(--text-muted);
  font-size: 1rem;
  margin-bottom: 2rem;
}

.price-box {
  background: rgba(11, 117, 255, 0.1);
  border: 2px solid var(--primary-color);
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.price-amount {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--primary-color);
}

.price-period {
  color: var(--text-muted);
  font-size: 0.95rem;
}

.benefits-list {
  list-style: none;
  padding: 0;
  margin: 0 0 2rem 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
  text-align: left;
}

.benefits-list li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: var(--text-light);
  font-size: 0.95rem;
}

.benefits-list i {
  color: var(--success-color);
  flex-shrink: 0;
}

/* ===================== ACTIVE SUBSCRIPTION ===================== */

.card-active {
  background: linear-gradient(135deg, #1a3a2a 0%, #162422 100%);
  border: 2px solid var(--success-color);
}

.subscription-status-active {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.status-header {
  display: flex;
  justify-content: center;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
}

.status-active {
  background: rgba(40, 167, 69, 0.2);
  color: var(--success-color);
  border: 1px solid var(--success-color);
}

.status-inactive {
  background: rgba(255, 43, 43, 0.2);
  color: var(--danger-color);
  border: 1px solid var(--danger-color);
}

.title-active {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--success-color);
  text-align: center;
}

.subscription-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  padding: 1.5rem;
  background: rgba(40, 167, 69, 0.1);
  border-radius: 8px;
}

.detail-item {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

.detail-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(40, 167, 69, 0.2);
  border-radius: 8px;
  color: var(--success-color);
  flex-shrink: 0;
}

.detail-content label {
  display: block;
  font-size: 0.85rem;
  color: var(--text-muted);
  margin-bottom: 0.25rem;
}

.detail-content p {
  color: #fff;
  font-weight: 600;
  margin: 0;
}

.price-highlight {
  color: var(--success-color);
  font-size: 1.25rem;
}

.subscription-note {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: rgba(11, 117, 255, 0.1);
  border-left: 3px solid var(--primary-color);
  border-radius: 4px;
  color: var(--text-light);
  font-size: 0.9rem;
}

.subscription-note i {
  color: var(--primary-color);
  flex-shrink: 0;
}

/* ===================== INACTIVE SUBSCRIPTION ===================== */

.card-inactive {
  background: linear-gradient(135deg, #3a2a1a 0%, #2a2017 100%);
  border: 2px solid var(--warning-color);
}

.subscription-status-inactive {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 1.5rem;
}

.title-inactive {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--warning-color);
}

.subtitle-inactive {
  color: var(--text-light);
  font-size: 1rem;
}

.reactivate-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1rem;
  background: rgba(255, 193, 7, 0.1);
  border-left: 3px solid var(--warning-color);
  border-radius: 4px;
  color: var(--text-light);
  font-size: 0.9rem;
  width: 100%;
}

.reactivate-note i {
  color: var(--warning-color);
  flex-shrink: 0;
}

/* ===================== BUTTONS ===================== */

.btn-subscribe {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.8rem 2rem;
  background: var(--primary-color);
  border: none;
  color: #fff;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.btn-subscribe:hover:not(:disabled) {
  background: #0a5fd8;
  box-shadow: 0 6px 20px rgba(11, 117, 255, 0.4);
  transform: translateY(-2px);
}

.btn-subscribe:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-cancel {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.7rem 1.5rem;
  background: transparent;
  border: 2px solid var(--warning-color);
  color: var(--warning-color);
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.95rem;
}

.btn-cancel:hover:not(:disabled) {
  background: var(--warning-color);
  color: #000;
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
  transform: translateY(-2px);
}

.btn-cancel:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===================== RESPONSIVE ===================== */

/* TABLET (768px and down) */
@media (max-width: 768px) {
  .suscripcion-page {
    padding: 1.5rem 1rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .card-body {
    padding: 1.5rem;
  }

  .price-box {
    padding: 1.5rem;
  }

  .price-amount {
    font-size: 2rem;
  }

  .subscription-details {
    grid-template-columns: 1fr;
  }

  .title-no-subscription,
  .title-active,
  .title-inactive {
    font-size: 1.5rem;
  }
}

/* MOBILE (576px and down) */
@media (max-width: 576px) {
  .suscripcion-page {
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

  .card-body {
    padding: 1.25rem;
  }

  .icon-empty {
    font-size: 3rem;
    margin-bottom: 1rem;
  }

  .title-no-subscription,
  .title-active,
  .title-inactive {
    font-size: 1.3rem;
  }

  .subtitle-no-subscription,
  .subtitle-inactive {
    font-size: 0.95rem;
  }

  .price-box {
    padding: 1.25rem;
    margin-bottom: 1.5rem;
  }

  .price-amount {
    font-size: 1.75rem;
  }

  .price-period {
    font-size: 0.9rem;
  }

  .benefits-list {
    margin-bottom: 1.5rem;
    gap: 0.75rem;
  }

  .benefits-list li {
    font-size: 0.9rem;
  }

  .subscription-details {
    padding: 1rem;
    gap: 1rem;
  }

  .detail-item {
    gap: 0.75rem;
  }

  .detail-icon {
    width: 36px;
    height: 36px;
    font-size: 0.9rem;
  }

  .detail-content label {
    font-size: 0.8rem;
  }

  .detail-content p {
    font-size: 0.95rem;
  }

  .price-highlight {
    font-size: 1.1rem;
  }

  .subscription-note,
  .reactivate-note {
    font-size: 0.85rem;
    padding: 0.75rem;
  }

  .btn-subscribe,
  .btn-cancel {
    width: 100%;
    padding: 0.7rem 1.25rem;
    font-size: 0.95rem;
  }

  .alert {
    font-size: 0.9rem;
    padding: 0.75rem;
  }
}

/* EXTRA SMALL (320px and down) */
@media (max-width: 320px) {
  .section-title {
    font-size: 1.1rem;
  }

  .title-no-subscription,
  .title-active,
  .title-inactive {
    font-size: 1.1rem;
  }

  .price-amount {
    font-size: 1.5rem;
  }

  .btn-subscribe,
  .btn-cancel {
    font-size: 0.85rem;
    padding: 0.6rem 1rem;
  }
}
</style>
