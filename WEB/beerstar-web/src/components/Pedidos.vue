<template>
  <div class="form-panel">
    <h2 class="text-2xl font-semibold mb-4">Gestión de Pedidos</h2>

    <div v-if="loading" class="text-center text-gray-500 mb-4">Cargando pedidos...</div>
    <div v-if="error" class="text-center text-red-500 mb-4">Error al cargar los pedidos: {{ error.message }}</div>

    <table class="min-w-full bg-white border border-gray-200 rounded-lg shadow-md mt-4">
      <thead class="bg-gray-100">
      <tr>
        <th class="py-2 px-4 text-left text-gray-600 font-semibold">ID Pedido</th>
        <th class="py-2 px-4 text-left text-gray-600 font-semibold">Cliente</th>
        <th class="py-2 px-4 text-left text-gray-600 font-semibold">Fecha</th>
        <th class="py-2 px-4 text-left text-gray-600 font-semibold">Estado</th>
        <th class="py-2 px-4 text-center text-gray-600 font-semibold">Total Ítems</th>
        <th class="py-2 px-4 text-center text-gray-600 font-semibold">Total</th>
        <th class="py-2 px-4 text-center text-gray-600 font-semibold">Acciones</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="pedido in pedidos" :key="pedido.id" class="border-t hover:bg-gray-50">
        <td class="py-2 px-4">{{ pedido.id }}</td>
        <td class="py-2 px-4">{{ pedido.userName || 'N/A' }}</td>
        <td class="py-2 px-4">{{ formatDate(pedido.orderDate) }}</td>
        <td class="py-2 px-4">{{ pedido.status }}</td>
        <td class="py-2 px-4 text-center">{{ getTotalItems(pedido.items) }}</td>
        <td class="py-2 px-4 text-center">{{ formatCurrency(pedido.totalAmount) }}</td>
        <td class="py-2 px-4 actions-cell">
          <button @click="showOrderDetail(pedido)" class="btn-view">Vista Previa</button>
          <button @click="openStatusModal(pedido)" class="btn-status">Cambiar Estado</button>
          <button @click="deleteOrder(pedido.id)" class="btn-delete">Eliminar</button>
        </td>
      </tr>
      <tr v-if="pedidos.length === 0 && !loading && !error">
        <td colspan="7" class="py-4 px-4 text-center text-gray-500">No hay pedidos para mostrar.</td>
      </tr>
      </tbody>
    </table>

    <div v-if="showDetailView && orderForDetail" class="modal-overlay" @click.self="closeDetailModal">
      <div class="modal-content">
        <button class="modal-close" @click="closeDetailModal">&times;</button>
        <h3>Detalles del Pedido #{{ orderForDetail.id }}</h3>
        <div class="detail-body">
          <p><strong>Cliente:</strong> {{ orderForDetail.userName || 'N/A' }}</p>
          <p><strong>Fecha del Pedido:</strong> {{ formatDate(orderForDetail.orderDate) }}</p>
          <p><strong>Estado:</strong> {{ orderForDetail.status }}</p>
          <p><strong>Método de Pago:</strong> {{ orderForDetail.paymentMethod }}</p>
          <p><strong>Dirección de Envío:</strong> {{ orderForDetail.shippingAddress }}</p>
          <p><strong>Total:</strong> {{ formatCurrency(orderForDetail.totalAmount) }}</p>
          <p><strong>Total Ítems:</strong> {{ getTotalItems(orderForDetail.items) }}</p>

          <h4 class="mt-4">Artículos del Pedido:</h4>
          <ul class="order-items-list">
            <li v-for="item in orderForDetail.items" :key="item.id">
              <div class="item-info">
                <img v-if="item.imagen" :src="item.imagen" :alt="item.productName" class="item-image-thumbnail" />
                <div class="item-details">
                  <p><strong>{{ item.productName }}</strong></p>
                  <p>Cantidad: {{ item.quantity }} | Precio Unitario: {{ formatCurrency(item.price) }}</p>
                  <p>Total Item: {{ formatCurrency(item.quantity * item.price) }}</p>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <div v-if="showStatusModal && orderToChangeStatus" class="modal-overlay" @click.self="closeStatusModal">
      <div class="modal-content">
        <button class="modal-close" @click="closeStatusModal">&times;</button>
        <h3>Cambiar Estado del Pedido #{{ orderToChangeStatus.id }}</h3>
        <div class="form-group">
          <label for="newStatus">Nuevo Estado:</label>
          <select id="newStatus" v-model="newOrderStatus" class="modal-select">
            <option value="PENDING">PENDING</option>
            <option value="PROCESSING">PROCESSING</option>
            <option value="SHIPPED">SHIPPED</option>
            <option value="DELIVERED">DELIVERED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        </div>
        <button @click="confirmChangeStatus" class="btn-confirm-status">Confirmar Cambio</button>
      </div>
    </div>

  </div>
</template>

<script>
import { getMyOrdersAsProvider, updatePedidoStatus, deletePedido } from '@/services/api.js';

export default {
  name: 'PedidosProveedor',
  data() {
    return {
      pedidos: [],
      loading: false,
      error: null,
      showDetailView: false,
      orderForDetail: null,
      showStatusModal: false,
      orderToChangeStatus: null,
      newOrderStatus: '',
    };
  },
  methods: {
    async fetchPedidos() {
      this.loading = true;
      this.error = null;
      try {
        // Assuming getMyOrdersAsProvider returns objects with 'userName'
        this.pedidos = await getMyOrdersAsProvider();
      } catch (err) {
        this.error = err;
        console.error('Error al cargar pedidos del proveedor:', err);
      } finally {
        this.loading = false;
      }
    },
    formatDate(dateArray) {
      if (!dateArray || !Array.isArray(dateArray) || dateArray.length < 3) return '';
      const date = new Date(dateArray[0], dateArray[1] - 1, dateArray[2], dateArray[3] || 0, dateArray[4] || 0, dateArray[5] || 0);
      return isNaN(date.getTime())
          ? String(dateArray)
          : date.toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    },
    formatCurrency(amount) {
      const num = parseFloat(amount);
      return isNaN(num) ? amount : `${num.toFixed(2)} €`;
    },
    getTotalItems(items) {
      if (!items || items.length === 0) return 0;
      return items.reduce((sum, item) => sum + item.quantity, 0);
    },
    showOrderDetail(pedido) {
      this.orderForDetail = pedido;
      this.showDetailView = true;
    },
    closeDetailModal() {
      this.showDetailView = false;
      this.orderForDetail = null;
    },
    openStatusModal(pedido) {
      this.orderToChangeStatus = pedido;
      this.newOrderStatus = pedido.status;
      this.showStatusModal = true;
    },
    closeStatusModal() {
      this.showStatusModal = false;
      this.orderToChangeStatus = null;
      this.newOrderStatus = '';
    },
    async confirmChangeStatus() {
      if (!this.orderToChangeStatus || !this.newOrderStatus) {
        alert('Por favor, selecciona un nuevo estado.');
        return;
      }
      if (confirm(`¿Estás seguro de cambiar el estado del pedido #${this.orderToChangeStatus.id} a "${this.newOrderStatus}"?`)) {
        try {
          await updatePedidoStatus(this.orderToChangeStatus.id, this.newOrderStatus);
          alert('Estado del pedido actualizado con éxito.');
          this.closeStatusModal();
          this.fetchPedidos();
        } catch (error) {
          console.error('Error al cambiar el estado del pedido:', error);
          alert('Hubo un error al actualizar el estado del pedido.');
        }
      }
    },
    async deleteOrder(id) {
      if (confirm('¿Estás seguro de eliminar este pedido? Esta acción es irreversible.')) {
        try {
          await deletePedido(id);
          alert('Pedido eliminado con éxito.');
          this.fetchPedidos();
        } catch (error) {
          console.error('Error al eliminar el pedido:', error);
          alert('Hubo un error al eliminar el pedido.');
        }
      }
    }
  },
  mounted() {
    this.fetchPedidos();
  }
};
</script>

<style scoped>
/* --- APLICACIÓN DE OPEN SANS A TODO EL COMPONENTE --- */
.form-panel {
  font-family:'Open Sans', sans-serif;
}

.form-panel h2,
.form-panel th,
.form-panel td,
.form-panel button,
.modal-content h3,
.modal-content p,
.modal-content label,
.modal-content select,
.modal-content button {
  font-family:'Open Sans', sans-serif;
}
/* --- FIN DE LA APLICACIÓN DE OPEN SANS --- */

/* Tus estilos CSS existentes */
.form-panel {
  background: white;
  padding: 3rem;
  border-radius: 20px;
  box-shadow: 0 6px 30px rgba(0,0,0,0.35);
  width: 100%;
  /* Make the table much narrower, leaving about 17cm (680px) on each side */
  max-width: calc(100% - 680px); /* 680px total margin (340px left + 340px right) */
  margin: auto; /* Keeps it centered */
  font-size: 1.1rem;
}

table {
  width: 100%;
  margin-top: 2rem;
  border-collapse: collapse;
  font-size: 1.25rem; /* Increased font size for the entire table */
}
th, td {
  border: 1px solid #ccc;
  padding: 1.5rem; /* Increased padding */
  text-align: left;
}
th {
  background-color: #eee;
}
/* Adjusted column targeting due to new 'Total Ítems' column */
td:nth-child(6) { /* The 'Total' column is now the 6th child */
  text-align: center;
}
th:last-child, td:last-child { /* Last column for Acciones */
  text-align: center;
}

.actions-cell {
  white-space: nowrap;
}

.actions-cell button {
  display: inline-block;
  width: auto;
  margin: 0 4px;
  padding: 0.4rem 0.8rem;
  font-size: 0.85rem;
  border-radius: 5px;
  cursor: pointer;
  border: none;
  color: white;
  transition: background-color 0.3s ease;
}

.btn-view {
  background-color: #17a2b8;
}
.btn-view:hover {
  background-color: #138496;
}

.btn-status {
  background-color: #007bff;
}
.btn-status:hover {
  background-color: #0056b3;
}

.btn-delete {
  background-color: #dc3545;
}
.btn-delete:hover {
  background-color: #c82333;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.4);
  position: relative;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  width: 90%;
}

.modal-close {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 5px;
  line-height: 1;
  color: #aaa;
  transition: color 0.2s ease;
}

.modal-close:hover {
  color: #666;
}

.detail-body {
  margin-top: 1rem;
}

.detail-body p {
  margin-bottom: 0.5rem;
}

.order-items-list {
  list-style: none;
  padding: 0;
  margin-top: 1rem;
}

.order-items-list li {
  border-bottom: 1px dashed #eee;
  padding: 0.5rem 0;
}

.order-items-list li:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-image-thumbnail {
  width: 50px;
  height: 50px;
  object-fit: contain;
  border-radius: 5px;
}

.no-image-placeholder {
  font-style: italic;
  color: #888;
}

.item-details p {
  margin: 0;
  font-size: 0.9em;
}

.modal-select {
  width: 100%;
  padding: 0.75rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  margin-top: 0.5rem;
}

.btn-confirm-status {
  display: block;
  width: 100%;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  border: none;
  background-color: #28a745;
  color: white;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
  margin-top: 1.5rem;
}

.btn-confirm-status:hover {
  background-color: #218838;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}
</style>