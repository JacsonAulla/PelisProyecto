// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import Inicio from '@/views/Inicio.vue'

const routes = [
  { path: '/', name: 'Inicio', component: Inicio },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
  
  // Rutas de usuario
  { path: '/mi-biblioteca', name: 'MiBiblioteca', component: () => import('@/views/MiBiblioteca.vue'), meta: { requiresAuth: true } },
  { path: '/pelicula/:id', name: 'PeliculaDetalle', component: () => import('@/views/PeliculaDetalle.vue') },
  { path: '/mi-suscripcion', name: 'MiSuscripcion', component: () => import('@/views/MiSuscripcion.vue'), meta: { requiresAuth: true } },
  
  // ← REPRODUCTOR (NUEVA RUTA)
  {
    path: '/pelicula/:id/reproducir',
    name: 'PeliculaReproducir',
    component: () => import('@/views/PeliculaReproducir.vue'),  // ← AQUÍ: Lazy load
    meta: { requiresAuth: true }
  },

  // --- RUTAS DE ADMIN ---
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/AdminDashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/usuarios',
    name: 'AdminUsuarios',
    component: () => import('@/views/admin/AdminUsuarios.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/usuarios/nuevo',
    name: 'AdminUsuarioNuevo',
    component: () => import('@/views/admin/AdminUsuarioForm.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/usuarios/editar/:id',
    name: 'AdminUsuarioEditar',
    component: () => import('@/views/admin/AdminUsuarioForm.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  
  // --- RUTAS DE PELÍCULAS ---
  {
    path: '/admin/peliculas',
    name: 'AdminPeliculas',
    component: () => import('@/views/admin/AdminPeliculas.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/peliculas/nuevo',
    name: 'AdminPeliculaNuevo',
    component: () => import('@/views/admin/AdminPeliculaForm.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/peliculas/editar/:id',
    name: 'AdminPeliculaEditar',
    component: () => import('@/views/admin/AdminPeliculaForm.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },

  // --- RUTAS DE GÉNEROS ---
  {
    path: '/admin/generos',
    name: 'AdminGeneros',
    component: () => import('@/views/admin/AdminGeneros.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({ 
  history: createWebHistory(), 
  routes 
})

// Guardia de navegación
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth
  const requiresAdmin = to.meta.requiresAdmin

  if (requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'Login' })
  } else if (requiresAdmin && !authStore.isAdmin) {
    next({ name: 'Inicio' })
  } else {
    next()
  }
})

export default router
