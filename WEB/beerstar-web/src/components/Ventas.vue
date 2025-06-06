<template>
  <div class="ventas-panel">
    <h2 class="text-2xl font-semibold mb-4">Órdenes de Venta del Proveedor</h2>

    <div v-if="loading" class="text-center text-gray-500 mb-4">Cargando órdenes...</div>
    <div v-if="error" class="text-center text-red-500 mb-4">Error al cargar: {{ error.message }}</div>

    <table v-if="ordenes.length > 0" class="min-w-full bg-white border border-gray-200 rounded-lg shadow-md mt-4">
      <thead class="bg-gray-100">
      <tr>
        <th class="py-2 px-4 text-left">ID</th>
        <th class="py-2 px-4 text-left">Fecha</th>
        <th class="py-2 px-4 text-left">Estado</th>
        <th class="py-2 px-4 text-left">Pedido Cliente</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="orden in ordenes" :key="orden.idOvp" class="border-t hover:bg-gray-50">
        <td class="py-2 px-4">{{ orden.idOvp }}</td>
        <td class="py-2 px-4">{{ formatDate(orden.fechaCreacion) }}</td>
        <td class="py-2 px-4">{{ orden.estado }}</td>
        <td class="py-2 px-4">{{ orden.pedidoCliente?.idPedido || 'N/A' }}</td>
      </tr>
      </tbody>
    </table>

    <div v-if="ordenes.length === 0 && !loading && !error" class="text-center text-gray-500 mt-4">
      No hay órdenes de venta para mostrar.
    </div>
  </div>
</template>

<script>

</script>

<style scoped>
.ventas-panel {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  max-width: 1000px;
  margin: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: 1px solid #ddd;
  padding: 1rem;
}
th {
  background-color: #f5f5f5;
  font-weight: bold;
}
</style>
