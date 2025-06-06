<template>
  <div class="admin-productos-single-layout">
    <div class="producto-form-panel">
      <h2>{{ loteSeleccionado ? 'Editar Lote' : 'Registrar Lote' }}</h2>

      <form @submit.prevent="loteSeleccionado ? actualizarLote() : registrarLote()">
        <input v-model="nombreLote" placeholder="Nombre del Lote" required/>
        <input v-model.number="precio" type="number" step="0.01" placeholder="Precio" required/>
        <input v-model.number="stock" type="number" placeholder="Stock disponible" required/>
        <textarea v-model="descripcion" placeholder="Descripción" required></textarea>
        <input v-model="url" placeholder="URL de la imagen"/>
        <button type="submit">{{ loteSeleccionado ? 'Actualizar' : 'Registrar' }}</button>
        <button v-if="loteSeleccionado" type="button" @click="cancelarEdicion">Cancelar</button>
      </form>
    </div>

    <div class="producto-list-panel">
      <h2>Listado de Lotes</h2>
      <table class="productos-table">
        <thead>
        <tr>
          <th>Imagen</th>
          <th>Nombre</th>
          <th>Precio</th>
          <th>Stock</th>
          <th>Descripción</th>
          <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="lote in paginatedLotes" :key="lote.id">
          <td>
            <img v-if="lote.url" :src="lote.url" :alt="lote.nombre" class="imagen-miniatura"/>
            <span v-else>Sin imagen</span>
          </td>
          <td>{{ lote.nombre }}</td>
          <td>{{ lote.precio }} €</td>
          <td>{{ lote.stock }}</td>
          <td>{{ lote.descripcion }}</td>
          <td class="actions-cell">
            <button @click="showDetail(lote)" class="btn-view">Vista</button>
            <button @click="editarLote(lote)" class="btn-edit">Editar</button>
            <button @click="borrarLote(lote.id)" class="btn-delete">Eliminar</button>
          </td>
        </tr>
        <tr v-if="paginatedLotes.length === 0 && lotes.length > 0">
          <td colspan="6" class="text-center">Página vacía. Ajusta la página.</td>
        </tr>
        <tr v-if="lotes.length === 0">
          <td colspan="6" class="text-center">No hay lotes registrados.</td>
        </tr>
        </tbody>
      </table>

      <div v-if="totalPages > 1" class="pagination-controls">
        <button @click="prevPage" :disabled="currentPage === 1" class="btn-pagination">
          &laquo; Página Anterior
        </button>
        <span>Página {{ currentPage }} de {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage >= totalPages" class="btn-pagination">
          Página Siguiente &raquo;
        </button>
      </div>

    </div>

    <div v-if="showDetailedView && loteForDetail" class="modal-overlay" @click.self="closeDetail">
      <div class="modal-content">
        <button class="modal-close" @click="closeDetail">&times;</button>
        <h3>{{ loteForDetail.nombre }}</h3>

        <div class="detail-body">
          <img v-if="loteForDetail.url" :src="loteForDetail.url" :alt="loteForDetail.nombre" class="detail-image"/>
          <span v-else class="no-image-placeholder">Sin imagen disponible</span>

          <div class="detail-info">
            <p><strong>Precio:</strong> {{ loteForDetail.precio }} €</p>
            <p><strong>Stock:</strong> {{ loteForDetail.stock }}</p>
            <p><strong>Descripción:</strong> {{ loteForDetail.descripcion }}</p>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
// El import está correcto: getMyBatches, createBatch, updateBatch, deleteBatch
import { getMyBatches, createBatch, updateBatch, deleteBatch } from '@/services/api';

export default {
  name: 'LotesAdminPaginatedSingle',
  data() {
    return {
      lotes: [],
      nombreLote: '', // Mantener para el v-model del formulario
      precio: null,
      descripcion: '',
      url: '', // Coincide con el campo 'url' en BatchDTO
      stock: null,
      loteSeleccionado: null,
      currentPage: 1,
      itemsPerPage: 8,
      showDetailedView: false,
      loteForDetail: null
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.lotes.length / this.itemsPerPage);
    },
    paginatedLotes() {
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      const endIndex = startIndex + this.itemsPerPage;
      return this.lotes.slice(startIndex, endIndex);
    }
  },
  created() {
    this.cargarLotes();
  },
  methods: {
    async cargarLotes() {
      try {
        // La llamada a getMyBatches() es correcta
        const lotes = await getMyBatches();
        this.lotes = lotes;

        const newTotalPages = Math.ceil(this.lotes.length / this.itemsPerPage);
        if (this.currentPage > newTotalPages) {
          this.currentPage = newTotalPages > 0 ? newTotalPages : 1;
        }
        if (this.lotes.length === 0) {
          this.currentPage = 1;
        }
      } catch (error) {
        console.error('Error cargando lotes:', error);
      }
    },
    async registrarLote() {
      if (!this.nombreLote || this.precio === null || !this.descripcion || this.stock === null) {
        alert('Por favor, completa todos los campos obligatorios.');
        return;
      }
      try {
        const nuevoLote = {
          nombre: this.nombreLote, // Coincide con el campo 'nombre' en BatchDTO
          precio: parseFloat(this.precio),
          descripcion: this.descripcion,
          url: this.url || null, // Coincide con el campo 'url' en BatchDTO
          stock: parseInt(this.stock),
        };
        console.log('Enviando nuevo lote:', nuevoLote);
        await createBatch(nuevoLote);
        alert('Lote registrado exitosamente');
        this.resetFormulario();
        this.cargarLotes();
      } catch (error) {
        console.error('Error registrando lote:', error);
        alert('Hubo un error al registrar el lote.');
      }
    },
    editarLote(lote) {
      this.closeDetail();
      this.loteSeleccionado = lote;
      this.nombreLote = lote.nombre; // ✅ CAMBIO: Usar lote.nombre
      this.precio = lote.precio;
      this.descripcion = lote.descripcion;
      this.url = lote.url;
      this.stock = lote.stock;
    },
    async actualizarLote() {
      if (!this.loteSeleccionado) return;
      if (!this.nombreLote || this.precio === null || !this.descripcion || this.stock === null) {
        alert('Por favor, completa todos los campos obligatorios.');
        return;
      }
      try {
        const loteActualizado = {
          nombre: this.nombreLote, // Coincide con el campo 'nombre' en BatchDTO
          precio: parseFloat(this.precio),
          descripcion: this.descripcion,
          url: this.url || null, // Coincide con el campo 'url' en BatchDTO
          stock: parseInt(this.stock),
        };
        // ✅ CAMBIO: Usar loteSeleccionado.id
        console.log('Enviando lote actualizado:', loteActualizado);
        await updateBatch(this.loteSeleccionado.id, loteActualizado);
        alert('Lote actualizado exitosamente');
        this.resetFormulario();
        this.cargarLotes();
      } catch (error) {
        console.error('Error actualizando lote:', error);
        alert('Hubo un error al actualizar el lote.');
      }
    },
    async borrarLote(id) {
      if (confirm('¿Estás seguro de eliminar este lote?')) {
        try {
          // ✅ CAMBIO: Usar loteForDetail.id y loteSeleccionado.id
          if (this.loteForDetail && this.loteForDetail.id === id) {
            this.closeDetail();
          }
          if (this.loteSeleccionado && this.loteSeleccionado.id === id) {
            this.resetFormulario();
          }
          await deleteBatch(id);
          alert('Lote eliminado');
          this.cargarLotes();
        } catch (error) {
          console.error('Error eliminando lote:', error);
          alert('Hubo un error al eliminar el lote.');
        }
      }
    },
    cancelarEdicion() {
      this.resetFormulario();
    },
    resetFormulario() {
      this.nombreLote = '';
      this.precio = null;
      this.descripcion = '';
      this.url = '';
      this.stock = null;
      this.loteSeleccionado = null;
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.resetFormulario();
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.resetFormulario();
      }
    },
    showDetail(lote) {
      this.resetFormulario();
      this.loteForDetail = lote;
      this.showDetailedView = true;
    },
    closeDetail() {
      this.showDetailedView = false;
      this.loteForDetail = null;
    }
  }
};
</script>

<style scoped>
/* Mantén tus estilos CSS existentes, no necesitan cambios. */
.admin-productos-single-layout {
  display: flex; /* Arrange children side-by-side */
  gap: 1cm; /* 1 cm separation between panels */
  padding: 20px 2cm; /* 20px top/bottom padding, ~2cm margin effect on left/right */
  /* Removed max-width: 1200px; to allow it to be wider */
  margin: 0 auto; /* Center the container horizontally */
  flex-wrap: wrap; /* Allow wrapping on smaller screens */
  /* Optional: Add box-sizing for predictable padding behavior */
  box-sizing: border-box;
  /* Needed for modal absolute positioning context if modal is inside this layout */
  position: relative;
}

/* Styles for the Form Panel */
.producto-form-panel {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  width: 100%; /* Base width */
  font-size: 1rem;
  text-align: center;
  flex: 1; /* Allows form to grow and take up available space */
  min-width: 300px; /* Minimum width before wrapping */
}

.producto-form-panel h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
}

.producto-form-panel form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.producto-form-panel input,
.producto-form-panel select,
.producto-form-panel textarea {
  padding: 0.75rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  width: 100%;
  box-sizing: border-box;
}

.producto-form-panel textarea {
  min-height: 120px;
  resize: vertical;
}

.producto-form-panel button {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  border: none;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
  width: auto;
  align-self: flex-start;
  margin-right: 0.5rem;
}

.producto-form-panel button[type="submit"] {
  background-color: #28a745;

}

.producto-form-panel button:hover {
  opacity: 0.9;
}

.producto-form-panel button:last-child {
  margin-right: 0;
}

.producto-form-panel button[type="button"] { /* Cancel button */
  background-color: #0c0c0c;

}

.producto-form-panel button[type="button"]:hover {
  background-color: #0c0c0c;
}


/* Styles for the List Panel */
.producto-list-panel {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  width: 100%; /* Base width */
  font-size: 1rem;
  flex: 2; /* Allows list to grow more than form (ratio 2:1) */
  min-width: 400px; /* Minimum width before wrapping */
  display: flex; /* Use flexbox for internal layout */
  flex-direction: column; /* Stack contents vertically */
}

.producto-list-panel h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
}

.productos-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1rem; /* Space below table, before pagination */
}

.productos-table th,
.productos-table td {
  border: 1px solid #ddd;
  padding: 0.75rem;
  text-align: left;
  vertical-align: middle; /* Vertically align content in cells */
}

.productos-table th {
  background-color: #f2f2f2;
  font-weight: bold;
  color: #555;
}

.productos-table tbody tr:nth-child(even) {
  background-color: #f9f9f9;
}

.productos-table tbody tr:hover {
  background-color: #e9e9e9;
}

/* Style for the actions cell to potentially use flex or control button layout */
.actions-cell {
  white-space: nowrap; /* Prevent buttons from wrapping */
}

/* Image miniature style */
.imagen-miniatura {
  height: 50px; /* Keep a consistent height for the row */
  width: auto; /* Allow width to adjust proportionally based on height */
  object-fit: contain; /* Scales the image down to fit within the dimensions without cropping */
  display: block; /* Treat as block element */
  margin: 0 auto; /* Horizontally center the block element */
  border-radius: 6px;
}


/* Styles for buttons inside table cells */
.productos-table td button {
  display: inline-block;
  width: auto;
  margin: 0 5px 0 0;
  padding: 0.4rem 0.8rem;
  font-size: 0.85rem;

  border-radius: 5px;
  cursor: pointer;
  border: none;
  color: white;
  transition: background-color 0.3s ease;
}

.productos-table td button:last-child {
  margin-right: 0;
}

/* Specific styles for the new "Vista" button */
.productos-table .btn-view {
  background-color: #17a2b8; /* Bootstrap info color */
}

.productos-table .btn-view:hover {
  background-color: #138496;
}

.productos-table .btn-edit {
  background-color: #ffc107;
}

.productos-table .btn-edit:hover {
  background-color: #e0a800;
}

.productos-table .btn-delete {
  background-color: #dc3545;
}

.productos-table .btn-delete:hover {
  background-color: #c82333;
}

.text-center {
  text-align: center;
}


/* Pagination Styles */
.pagination-controls {
  display: flex;
  justify-content: center; /* Center controls horizontally */
  align-items: center;
  margin-top: 1.5rem; /* Space above controls */
  gap: 1rem; /* Space between buttons and text */
}

.btn-pagination {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: #f8f9fa;
  color: #333;
  cursor: pointer;
  transition: background-color 0.3s ease, border-color 0.3s ease;
  font-size: 1rem;
}

.btn-pagination:hover:not(:disabled) {
  background-color: #e9ecef;
  border-color: #bbb;
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #f1f1f1;
}

.pagination-controls span {
  font-weight: bold;
  color: #555;
  white-space: nowrap;
}

.modal-overlay {
  position: fixed; /* Fixed position relative to the viewport */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7); /* Semi-transparent dark background */
  display: flex;
  justify-content: center; /* Center content horizontally */
  align-items: center; /* Center content vertically */
  z-index: 1000; /* Ensure it's on top of other content */
}

/* The modal content box */
.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.4);
  position: relative; /* Needed for positioning the close button */
  max-width: 500px; /* Limit the width */
  max-height: 90vh; /* Limit the height to prevent overflow, allow scrolling */
  overflow-y: auto; /* Add scroll if content exceeds max-height */
  width: 90%; /* Take up 90% of the available width */
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
  text-align: center; /* Center the title */
}

/* Close button for the modal */
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
  display: flex;
  flex-direction: column; /* Stack image and info vertically */
  align-items: center; /* Center items horizontally within the body */
  gap: 1.5rem; /* Space between image and info */
}

/* Image within the detailed view */
.detail-image {
  max-width: 100%; /* Ensure image doesn't overflow container */
  max-height: 251px; /* Limit image height */
  height: auto; /* Maintain aspect ratio */
  display: block; /* Treat as block for centering */
  margin: 0 auto; /* Center the image horizontally */
  object-fit: contain; /* Ensure entire image is visible */
  border-radius: 8px; /* Match panel styling */
  border: 1px solid #eee; /* Subtle border */
}

.no-image-placeholder {
  display: block;
  text-align: center;
  font-style: italic;
  color: #777;
  margin: 1rem 0;
}

/* Detailed information text block */
.detail-info {
  width: 100%; /* Take full width below image */
}

.detail-info p {
  margin-bottom: 0.75rem; /* Space between detail lines */
  line-height: 1.5;
  color: #555;
}

.detail-info p:last-child {
  margin-bottom: 0;
}

.detail-info strong {
  color: #333;
}
</style>