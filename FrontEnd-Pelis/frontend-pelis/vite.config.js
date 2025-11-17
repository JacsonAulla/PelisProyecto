import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // cualquier llamada que empiece con /api irá al backend
      '/api': 'http://localhost:8080'
    }
  },
  resolve: {
    alias: {
      '@': '/src'
    }
  }
})


