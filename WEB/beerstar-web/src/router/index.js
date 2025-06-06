import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import RegistroArticulosLayout from '../views/RegistroArticulos.vue';
import Productos from '../components/Productos.vue';
import Pedidos from '../components/Pedidos.vue';
import Ventas from '../components/Ventas.vue';

const routes = [
    { path: '/', name: 'Login', component: Login },
    {
        path: '/gestion',
        name: 'GestionProveedor',  // solo nombre interno, no componente
        component: RegistroArticulosLayout,
        children: [
            {
                path: '',
                name: 'GestionProductos',
                component: Productos
            },
            {
                path: 'pedidos',
                name: 'GestionPedidos',
                component: Pedidos
            },
            {
                path: 'ventas',
                name: 'GestionVentas',
                component: Ventas,
                props: () => ({
                    idProveedor: parseInt(localStorage.getItem('idProveedor')) || 0
                })
            },
            {
                path: 'lotes',
                name: 'GestionLotes',
                component: () => import('../components/lotes.vue') // Lazy load
            }
        ]
    },
    { path: '/registro', redirect: '/gestion' },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
