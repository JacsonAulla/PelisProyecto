import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

const getAuthHeader = () => {
    const token = localStorage.getItem('token');
    return { headers: { Authorization: `Bearer ${token}` } };
};

export default {
    // Traer películas (Paginado)
    async getPeliculas(page = 0, size = 10) {
        const url = `${BASE_URL}/peliculas?page=${page}&size=${size}&sort=anioLanzamiento,desc`;
        const response = await axios.get(url, getAuthHeader());
        return response.data;
    },

    // --- NUEVO: Traer una sola película por ID ---
    async getById(id) {
        const response = await axios.get(`${BASE_URL}/peliculas/${id}`, getAuthHeader());
        return response.data;
    }
};