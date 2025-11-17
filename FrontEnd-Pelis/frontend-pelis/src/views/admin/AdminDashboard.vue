<template>
  <div class="dashboard-page">
    <!-- HEADER -->
    <div class="header-section">
      <h1 class="text-white">
        <i class="fa-solid fa-chart-line me-2"></i>
        Panel de Control
      </h1>
      <p class="header-subtitle">Selecciona una sección para continuar</p>
    </div>

    <!-- BOTONES DE NAVEGACIÓN -->
    <div class="nav-crudds-dashboard">
      <!-- USUARIOS -->
      <router-link 
        :to="{ name: 'AdminUsuarios' }" 
        class="btn-nav-dashboard usuarios"
      >
        <i class="fa-solid fa-users"></i>
        <span>Gestionar Usuarios</span>
      </router-link>

      <!-- PELÍCULAS -->
      <router-link 
        :to="{ name: 'AdminPeliculas' }" 
        class="btn-nav-dashboard peliculas"
      >
        <i class="fa-solid fa-film"></i>
        <span>Gestionar Películas</span>
      </router-link>

      <!-- GÉNEROS -->
      <router-link 
        :to="{ name: 'AdminGeneros' }" 
        class="btn-nav-dashboard generos"
      >
        <i class="fa-solid fa-tag"></i>
        <span>Gestionar Géneros</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
// No requiere lógica
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

.dashboard-page {
  width: 100%;
  background-color: #000; /* fondo negro */
  color: white; /* texto blanco */
  padding: 0;
  margin: 0;
  min-height: 100vh; /* ocupa toda la pantalla */
}

/* ===================== HEADER ===================== */

.header-section {
  padding: 2rem 2rem 1rem 2rem;
  margin: 0;
}

.header-section h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  color: #fff;
  display: flex;
  align-items: center;
}

.header-section h1 i {
  color: var(--primary-color);
}

.header-subtitle {
  margin: 0.5rem 0 0 0;
  font-size: 0.95rem;
  color: #fff !important;
  font-weight: 500;
}

/* ===================== NAV DASHBOARD ===================== */

.nav-crudds-dashboard {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  padding: 2rem 2rem;
  margin: 0;
}

.btn-nav-dashboard {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1rem 1.75rem;
  background: rgba(11, 117, 255, 0.15);
  border: 2px solid rgba(11, 117, 255, 0.3);
  color: #0b75ff;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 700;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.btn-nav-dashboard:hover {
  background: rgba(11, 117, 255, 0.25);
  border-color: #0b75ff;
  color: #fff;
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(11, 117, 255, 0.3);
}

.btn-nav-dashboard i {
  font-size: 1.3rem;
  color: inherit;
}

.btn-nav-dashboard span {
  display: inline;
}

/* ===================== RESPONSIVE ===================== */

@media (max-width: 768px) {
  .header-section {
    padding: 1rem 1.5rem 0.5rem 1.5rem;
  }

  .header-section h1 {
    font-size: 1.4rem;
  }

  .header-subtitle {
    font-size: 0.9rem;
  }

  .nav-crudds-dashboard {
    padding: 1.5rem;
    gap: 0.75rem;
  }

  .btn-nav-dashboard {
    padding: 0.75rem 1.25rem;
    font-size: 0.9rem;
  }

  .btn-nav-dashboard i {
    font-size: 1.1rem;
  }
}

@media (max-width: 576px) {
  .header-section {
    padding: 0.75rem 1rem 0.5rem 1rem;
  }

  .header-section h1 {
    font-size: 1.2rem;
  }

  .header-section h1 i {
    display: none;
  }

  .header-subtitle {
    font-size: 0.85rem;
  }

  .nav-crudds-dashboard {
    padding: 1rem;
    gap: 0.5rem;
    flex-direction: column;
  }

  .btn-nav-dashboard {
    width: 100%;
    padding: 0.75rem 1rem;
    font-size: 0.85rem;
  }

  .btn-nav-dashboard i {
    font-size: 1rem;
  }
}
</style>
