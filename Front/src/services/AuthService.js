import axios from 'axios';

const API_URL = 'http://localhost:8080/auth';

export default {
    async login(identifier, password) {
        const response = await axios.post(`${API_URL}/login`, { identifier, password });
        return response.data;
    },

    async loginWithGoogle(googleToken) {
        const response = await axios.post(`${API_URL}/google`, { token: googleToken });
        return response.data;
    },

    // --- REGISTRO (NUEVO) ---

    // Paso 1: Enviar datos iniciales
    async registerInit(user) {
        // user debe tener: email, username, fechaNacimiento
        const response = await axios.post(`${API_URL}/register`, user);
        return response.data;
    },

    // Paso 2: Enviar código de verificación
    async verifyCode(email, code) {
        const response = await axios.post(`${API_URL}/verify`, {
            email,
            verificationCode: code
        });
        return response.data;
    },

    // Paso 3: Crear contraseña
    async setPassword(email, password) {
        const response = await axios.post(`${API_URL}/set-password`, {
            email,
            password
        });
        return response.data;
    }
};