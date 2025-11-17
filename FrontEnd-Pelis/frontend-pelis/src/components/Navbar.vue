<template>
  <header class="site-navbar">
    <div class="nav-inner">
      <!-- NAV LEFT: Logo + Brand -->
      <div class="nav-left">
        <router-link to="/" class="logo-link">
          <img 
            src="/peliculas/logo.png" 
            alt="StreamsUTP" 
            class="nav-logo"
            @error="logoError"
          />
          <span class="nav-brand">StreamsUTP</span>
        </router-link>
      </div>

      <!-- NAV CENTER: Main Links (Desktop) -->
      <nav class="nav-center">
        <router-link to="/" class="nav-link">
          <i class="fa-solid fa-home me-1"></i>
          <span>INICIO</span>
        </router-link>

        <router-link 
          v-if="authStore.isLoggedIn" 
          to="/mi-biblioteca" 
          class="nav-link"
        >
          <i class="fa-solid fa-book me-1"></i>
          <span>BIBLIOTECA</span>
        </router-link>

        <router-link 
          v-if="authStore.isLoggedIn" 
          to="/mi-suscripcion" 
          class="nav-link"
        >
          <i class="fa-solid fa-star me-1"></i>
          <span>SUSCRIPCIÓN</span>
        </router-link>

        <router-link 
          v-if="authStore.isLoggedIn && authStore.isAdmin" 
          to="/admin" 
          class="nav-link text-warning"
        >
          <i class="fa-solid fa-user-shield me-1"></i>
          <span>ADMIN</span>
        </router-link>
      </nav>

      <!-- NAV RIGHT: Auth Buttons + Mobile Menu Toggle -->
      <div class="nav-right">
        <!-- Desktop Auth Buttons -->
        <template v-if="!authStore.isLoggedIn">
          <router-link to="/login" class="btn btn-primary btn-sm btn-login">
            <i class="fa-solid fa-sign-in-alt me-1"></i>
            <span>Iniciar Sesión</span>
          </router-link>
          <router-link to="/register" class="btn btn-danger btn-sm btn-register">
            <i class="fa-solid fa-user-plus me-1"></i>
            <span>Registrarse</span>
          </router-link>
        </template>

        <!-- Desktop User Menu -->
        <div v-if="authStore.isLoggedIn" class="user-menu-desktop">
          <button class="btn-user" @click="toggleUserMenu">
            <i class="fa-solid fa-user-circle"></i>
            <span class="user-name">{{ authStore.user?.nombre || 'Usuario' }}</span>
            <i class="fa-solid fa-chevron-down" :class="{ rotated: showUserMenu }"></i>
          </button>

          <!-- Dropdown Menu - Simple positioned -->
          <div v-if="showUserMenu" class="dropdown-menu">
            <div class="dropdown-header">
              <span class="user-email">{{ authStore.user?.email }}</span>
            </div>
            
            <router-link 
              to="/mi-biblioteca" 
              class="dropdown-item"
              @click="showUserMenu = false"
            >
              <i class="fa-solid fa-book me-2"></i>
              Mi Biblioteca
            </router-link>

            <router-link 
              to="/mi-suscripcion" 
              class="dropdown-item"
              @click="showUserMenu = false"
            >
              <i class="fa-solid fa-star me-2"></i>
              Mi Suscripción
            </router-link>

            <div class="dropdown-divider"></div>

            <button 
              @click="handleLogout" 
              class="dropdown-item btn-logout-menu"
            >
              <i class="fa-solid fa-sign-out-alt me-2"></i>
              Cerrar Sesión
            </button>
          </div>
        </div>

        <!-- Mobile Menu Toggle -->
        <button 
          class="btn-mobile-toggle" 
          @click="showMobileMenu = !showMobileMenu"
          :class="{ active: showMobileMenu }"
          aria-label="Abrir menú"
        >
          <i class="fa-solid fa-bars"></i>
        </button>
      </div>
    </div>

    <!-- MOBILE MENU (Overlay) -->
    <div 
      v-if="showMobileMenu" 
      class="mobile-menu"
      @click="showMobileMenu = false"
    >
      <div class="mobile-menu-content" @click.stop>
        <!-- Mobile Links -->
        <router-link 
          to="/" 
          class="mobile-link"
          @click="showMobileMenu = false"
        >
          <i class="fa-solid fa-home"></i>
          <span>INICIO</span>
        </router-link>

        <router-link 
          v-if="authStore.isLoggedIn" 
          to="/mi-biblioteca" 
          class="mobile-link"
          @click="showMobileMenu = false"
        >
          <i class="fa-solid fa-book"></i>
          <span>MI BIBLIOTECA</span>
        </router-link>

        <router-link 
          v-if="authStore.isLoggedIn" 
          to="/mi-suscripcion" 
          class="mobile-link"
          @click="showMobileMenu = false"
        >
          <i class="fa-solid fa-star"></i>
          <span>SUSCRIPCIÓN</span>
        </router-link>

        <router-link 
          v-if="authStore.isLoggedIn && authStore.isAdmin" 
          to="/admin" 
          class="mobile-link text-warning"
          @click="showMobileMenu = false"
        >
          <i class="fa-solid fa-user-shield"></i>
          <span>ADMIN</span>
        </router-link>

        <div class="mobile-menu-divider"></div>

        <!-- Mobile Auth Buttons -->
        <template v-if="!authStore.isLoggedIn">
          <router-link 
            to="/login" 
            class="mobile-link"
            @click="showMobileMenu = false"
          >
            <i class="fa-solid fa-sign-in-alt"></i>
            <span>INICIAR SESIÓN</span>
          </router-link>
          <router-link 
            to="/register" 
            class="mobile-link"
            @click="showMobileMenu = false"
          >
            <i class="fa-solid fa-user-plus"></i>
            <span>REGISTRARSE</span>
          </router-link>
        </template>

        <!-- Mobile Logout -->
        <button 
          v-if="authStore.isLoggedIn" 
          @click="handleLogoutMobile" 
          class="mobile-link btn-logout-mobile"
        >
          <i class="fa-solid fa-sign-out-alt"></i>
          <span>CERRAR SESIÓN</span>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const showUserMenu = ref(false)
const showMobileMenu = ref(false)

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const handleLogout = () => {
  authStore.logout()
  showUserMenu.value = false
  showMobileMenu.value = false
}

const handleLogoutMobile = () => {
  handleLogout()
  showMobileMenu.value = false
}

const logoError = (e) => {
  e.target.src = '/vite.svg'
}
</script>

<style scoped>
/* ===================== VARIABLES ===================== */

:root {
  --primary-color: #0b75ff;
  --danger-color: #ff2b2b;
  --navbar-bg: #2b2b2b;
  --text-light: rgba(255, 255, 255, 0.9);
  --text-muted: rgba(255, 255, 255, 0.6);
  --border-color: rgba(0, 0, 0, 0.6);
}

* {
  box-sizing: border-box;
}

/* ===================== NAVBAR ===================== */

.site-navbar {
  background: var(--navbar-bg);
  color: #fff;
  height: 56px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px var(--border-color);
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
}

.nav-inner {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

/* ===================== NAV LEFT ===================== */

.nav-left {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  flex-shrink: 0;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  text-decoration: none;
  transition: opacity 0.2s;
}

.logo-link:hover {
  opacity: 0.8;
}

.nav-logo {
  height: 32px;
  width: auto;
  object-fit: contain;
}

.nav-brand {
  font-weight: 700;
  font-size: 1rem;
  color: #fff;
  letter-spacing: 1px;
  white-space: nowrap;
}

/* ===================== NAV CENTER ===================== */

.nav-center {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
  justify-content: center;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  color: var(--text-light);
  text-decoration: none;
  font-weight: 600;
  font-size: 0.85rem;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.nav-link:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-link.router-link-active {
  color: #fff;
  background-color: rgba(11, 117, 255, 0.2);
  border-bottom: 2px solid var(--primary-color);
}

.nav-link i {
  font-size: 0.9rem;
}

.text-warning {
  color: #ffc107 !important;
}

/* ===================== NAV RIGHT ===================== */

.nav-right {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-shrink: 0;
}

/* Auth Buttons */
.btn-login,
.btn-register {
  display: flex !important;
  align-items: center;
  gap: 0.3rem;
  padding: 0.4rem 0.8rem !important;
  border-radius: 20px !important;
  font-size: 0.8rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  white-space: nowrap;
  border: none !important;
}

.btn-login {
  background: var(--primary-color) !important;
  color: #fff !important;
}

.btn-login:hover {
  background: #0a5fd8 !important;
  box-shadow: 0 4px 12px rgba(11, 117, 255, 0.3);
}

.btn-register {
  background: var(--danger-color) !important;
  color: #fff !important;
}

.btn-register:hover {
  background: #e60a0a !important;
  box-shadow: 0 4px 12px rgba(255, 43, 43, 0.3);
}

/* User Menu */
.user-menu-desktop {
  position: relative;
}

.btn-user {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: none;
  border: none;
  color: var(--text-light);
  font-weight: 600;
  cursor: pointer;
  padding: 0.5rem 0.75rem;
  border-radius: 4px;
  transition: all 0.2s ease;
  font-size: 0.85rem;
}

.btn-user:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.1);
}

.btn-user i:first-child {
  font-size: 1.2rem;
}

.btn-user i:last-child {
  font-size: 0.7rem;
  margin-left: 0.25rem;
  transition: transform 0.2s;
}

.btn-user i.rotated {
  transform: rotate(180deg);
}

.user-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Dropdown Menu - SIMPLE */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  background: var(--navbar-bg);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  min-width: 220px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);
  z-index: 2000;
  overflow: hidden;
  display: block !important;  /* ← AGREGA ESTA LÍNEA */
  visibility: visible !important;  /* ← Y ESTA */
  opacity: 1 !important;  /* ← Y ESTA */
}

.dropdown-header {
  padding: 1rem;
  background: rgba(11, 117, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.user-email {
  display: block;
  font-size: 0.85rem;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-item {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  color: var(--text-light);
  text-decoration: none;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.dropdown-item:hover {
  background-color: rgba(11, 117, 255, 0.2);
  color: #fff;
}

.dropdown-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 0.25rem 0;
}

.btn-logout-menu {
  color: #ff6b6b;
}

.btn-logout-menu:hover {
  background-color: rgba(255, 43, 43, 0.2) !important;
  color: #ff6b6b;
}

/* Mobile Toggle */
.btn-mobile-toggle {
  display: none;
  background: none;
  border: none;
  color: #fff;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0.5rem;
  transition: all 0.2s ease;
}

.btn-mobile-toggle:hover,
.btn-mobile-toggle.active {
  color: var(--primary-color);
}

/* ===================== MOBILE MENU ===================== */

.mobile-menu {
  display: none;
  position: fixed;
  top: 56px;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 999;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.mobile-menu-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  background: var(--navbar-bg);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  animation: slideDown 0.3s ease-out;
  max-height: calc(100vh - 56px);
  overflow-y: auto;
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

.mobile-link {
  display: flex;
  align-items: center;
  gap: 1rem;
  width: 100%;
  padding: 1rem 1.5rem;
  background: none;
  border: none;
  color: var(--text-light);
  text-decoration: none;
  cursor: pointer;
  text-align: left;
  font-weight: 500;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.mobile-link:hover,
.mobile-link.router-link-active {
  background-color: rgba(11, 117, 255, 0.15);
  color: #fff;
  border-left-color: var(--primary-color);
  padding-left: 1.25rem;
}

.mobile-link i {
  width: 20px;
  text-align: center;
  font-size: 1.1rem;
}

.mobile-menu-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 0.5rem 0;
}

.btn-logout-mobile {
  color: var(--danger-color);
}

.btn-logout-mobile:hover {
  background-color: rgba(255, 43, 43, 0.15);
  color: #ff6b6b;
  border-left-color: var(--danger-color);
}

/* ===================== RESPONSIVE ===================== */

@media (max-width: 768px) {
  .nav-center {
    display: none;
  }

  .btn-login span,
  .btn-register span {
    display: none;
  }

  .btn-login,
  .btn-register {
    padding: 0.4rem 0.6rem !important;
  }

  .user-name {
    display: none;
  }

  .btn-user {
    padding: 0.4rem 0.5rem;
  }
}

@media (max-width: 576px) {
  .site-navbar {
    height: 50px;
  }

  .nav-inner {
    padding: 0 0.75rem;
    gap: 0.5rem;
  }

  .nav-logo {
    height: 28px;
  }

  .nav-brand {
    font-size: 0.9rem;
  }

  .btn-mobile-toggle {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .nav-center {
    display: none;
  }

  .btn-login,
  .btn-register {
    display: none !important;
  }

  .user-menu-desktop {
    display: none;
  }

  .mobile-menu {
    display: block;
  }

  .mobile-link {
    padding: 0.85rem 1.25rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 320px) {
  .nav-brand {
    display: none;
  }

  .nav-logo {
    height: 24px;
  }

  .nav-inner {
    padding: 0 0.5rem;
  }
}
</style>
