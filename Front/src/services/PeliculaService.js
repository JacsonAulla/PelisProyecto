import axios from 'axios';

const API_URL = 'http://localhost:8080/admin/peliculas';

const getAuthHeader = () => {
    const token = localStorage.getItem('token');
    return { headers: { Authorization: `Bearer ${token}` } };
};

export default {
    // Listar (Paginado)
    async getAll(page = 0, size = 10) {
        const response = await axios.get(`${API_URL}?page=${page}&size=${size}&sort=id,desc`, getAuthHeader());
        return response.data;
    },

    // Crear
    async create(pelicula) {
        const response = await axios.post(API_URL, pelicula, getAuthHeader());
        return response.data;
    },

    // Editar
    async update(id, pelicula) {
        const response = await axios.put(`${API_URL}/${id}`, pelicula, getAuthHeader());
        return response.data;
    },

    // Eliminar
    async delete(id) {
        const response = await axios.delete(`${API_URL}/${id}`, getAuthHeader());
        return response.data;
    }
};