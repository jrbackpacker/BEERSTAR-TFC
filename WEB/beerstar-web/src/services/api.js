import axios from 'axios';


const API_BASE_URL = 'https://tfc-beerstar.onrender.com'; // O tu URL de Railway

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor para añadir el token JWT a las solicitudes
api.interceptors.request.use(
    (config) => {
        // Solo agregar token si no es login
        if (config.url !== '/api/auth/login' && !config.url.startsWith('/api/public')) {
            const token = localStorage.getItem('token'); // Asume que guardas el token aquí
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Funciones de Autenticación
export const loginUser = async (email, password) => {
    try {
        const response = await api.post('/api/auth/login', { email, password });
        return response.data;
    } catch (error) {
        console.error('Error al hacer login:', error);
        throw error;
    }
};

// ✅ AÑADIDO: Función para registrar un nuevo usuario
export const registerUser = async (userData) => {
    try {
        const response = await api.post('/api/auth/register', userData);
        return response.data;
    } catch (error) {
        console.error('Error al registrar usuario:', error);
        throw error;
    }
};

// ==============================================================================
// GESTIÓN DE ARTÍCULOS (CERVEZAS)
// ==============================================================================

// Para que los clientes o administradores vean TODAS las cervezas
export const getAllArticulos = async () => {
    try {
        const response = await api.get('/api/beers');
        return response.data;
    } catch (error) {
        console.error('Error al obtener todos los artículos:', error);
        throw error;
    }
};



// Para que el proveedor obtenga SOLO sus artículos
export const getMyArticulos = async () => {
    try {
        const response = await api.get('/api/beers/my-beers');
        return response.data;
    } catch (error) {
        console.error('Error al obtener mis artículos:', error);
        throw error;
    }
};

export const registrarArticulo = async (articulo) => {
    try {
        const response = await api.post('/api/beers', articulo);
        return response.data;
    } catch (error) {
        console.error('Error al registrar artículo:', error);
        throw error;
    }
};

export const eliminarArticulo = async (id) => {
    try {
        const response = await api.delete(`/api/beers/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error al eliminar artículo:', error);
        throw error;
    }
};

export const actualizarArticulo = async (id, articulo) => {
    try {
        const response = await api.put(`/api/beers/${id}`, articulo);
        return response.data;
    } catch (error) {
        console.error('Error al actualizar artículo:', error);
        throw error;
    }
};

// ==============================================================================
// GESTIÓN DE LOTES (BATCHES)
// ==============================================================================

// Para que los clientes o administradores vean TODOS los lotes
export const getAllBatches = async () => {
    try {
        const response = await api.get('/api/batches');
        return response.data;
    } catch (error) {
        console.error('Error al obtener todos los lotes:', error);
        throw error;
    }
};

// ✅ AÑADIDO: Para obtener un lote por su ID
export const getBatchById = async (id) => {
    try {
        const response = await api.get(`/api/batches/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error al obtener lote con ID ${id}:`, error);
        throw error;
    }
};

// Para que el proveedor obtenga SOLO sus lotes
export const getMyBatches = async () => {
    try {
        const response = await api.get('/api/batches/my-batches');
        return response.data;
    } catch (error) {
        console.error('Error al obtener mis lotes:', error);
        throw error;
    }
};

export const createBatch = async (batchData) => {
    try {
        const response = await api.post('/api/batches', batchData);
        return response.data;
    } catch (error) {
        console.error('Error al registrar lote:', error);
        throw error;
    }
};

export const updateBatch = async (id, batchData) => {
    try {
        const response = await api.put(`/api/batches/${id}`, batchData);
        return response.data;
    } catch (error) {
        console.error(`Error al actualizar lote con ID ${id}:`, error);
        throw error;
    }
};

export const deleteBatch = async (id) => {
    try {
        await api.delete(`/api/batches/${id}`);
        return true;
    } catch (error) {
        console.error(`Error al eliminar lote con ID ${id}:`, error);
        throw error;
    }
};

// ==============================================================================
// GESTIÓN DE PEDIDOS (ORDERS)
// ==============================================================================

// Para que un CLIENTE vea sus propios pedidos (asumiendo que el backend filtra por usuario autenticado)
export const getPedidosCliente = async () => {
    try {
        const response = await api.get('/api/orders');
        return response.data;
    } catch (error) {
        console.error('Error al obtener pedidos del cliente:', error);
        throw error;
    }
};

// ✅ AÑADIDO: Para que el PROVEEDOR vea los pedidos que contienen SUS productos
// Asume que el backend tiene un endpoint como /api/orders/my-orders-as-provider
export const getMyOrdersAsProvider = async () => {
    try {
        const response = await api.get('/api/orders/my-orders-as-provider');
        return response.data;
    } catch (error) {
        console.error('Error al obtener mis pedidos como proveedor:', error);
        throw error;
    }
};

export const getPedidoById = async (id) => {
    try {
        const response = await api.get(`/api/orders/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching order ${id}:`, error);
        throw error;
    }
};

export const updatePedidoStatus = async (id, newStatus) => {
    try {
        // Asumiendo que tu backend en /api/orders/{id}/status (PATCH) espera un objeto { status: newStatus }
        const response = await api.patch(`/api/orders/${id}/status`, { status: newStatus });
        return response.data;
    } catch (error) {
        console.error(`Error updating order status for ${id}:`, error);
        throw error;
    }
};

export const updatePedido = async (id, updatedOrderData) => {
    try {
        const response = await api.put(`/api/orders/${id}`, updatedOrderData);
        return response.data;
    } catch (error) {
        console.error(`Error updating order ${id}:`, error);
        throw error;
    }
};

export const deletePedido = async (id) => {
    try {
        await api.delete(`/api/orders/${id}`);
        return true;
    } catch (error) {
        console.error(`Error deleting order ${id}:`, error);
        throw error;
    }
};

// ==============================================================================
// GESTIÓN DE USUARIOS / PROVEEDORES PÚBLICOS
// ==============================================================================
export const getAllUsers = async () => {
    try {
        const response = await api.get('/api/users');
        return response.data;
    } catch (error) {
        console.error('Error al obtener todos los usuarios:', error);
        throw error;
    }
};

export const getUserProfile = async () => {
    try {
        const response = await api.get('/api/users/me'); // Endpoint para obtener el perfil del usuario autenticado
        return response.data;
    } catch (error) {
        console.error('Error al obtener el perfil del usuario:', error);
        throw error;
    }
};

export const getPublicProviders = async () => {
    try {
        const response = await api.get('/api/public/providers');
        return response.data;
    } catch (error) {
        console.error('Error al obtener proveedores públicos:', error);
        throw error;
    }
};

// Si tienes funciones para crear, actualizar, eliminar usuarios por admin
export const getUserById = (id) => api.get(`/api/users/${id}`);
export const createUserByAdmin = (userData) => api.post('/api/users', userData);
export const updateUser = (id, userData) => api.put(`/api/users/${id}`, userData);
export const deleteUser = (id) => api.delete(`/api/users/${id}`);

export default api;