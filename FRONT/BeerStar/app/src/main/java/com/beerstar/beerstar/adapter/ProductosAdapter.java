package com.beerstar.beerstar.adapter;

// Importaciones necesarias para el funcionamiento de vistas, botones e imágenes
import android.app.AlertDialog;
import android.util.Log; // Importar la clase Log
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.response.responseProductos;
import com.beerstar.beerstar.response.responseCarrito;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.util.List;

// Adaptador para mostrar una lista de productos individuales en un RecyclerView
public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {

    // Lista de productos que se mostrarán en la tienda
    private List<responseProductos> listaProductos;

    // Callback que se ejecuta cuando se añade un producto al carrito
    private Runnable onProductoAñadido;

    private static final String TAG = "ProductosAdapterLog"; // Etiqueta para los logs

    // Mapa para convertir id_categoria a nombre de categoría
    private static final SparseArray<String> categoryNames = new SparseArray<>();

    // Inicializar el mapa de categorías (bloque estático para que se cargue una vez)
    static {
        categoryNames.put(1, "Roja");
        categoryNames.put(2, "Ámbar");
        categoryNames.put(3, "Clara");
        categoryNames.put(4, "Lager");
        categoryNames.put(5, "Negra");
    }

    // Constructor del adaptador que recibe la lista de productos y el callback
    public ProductosAdapter(List<responseProductos> lista, Runnable onProductoAñadido) {
        this.listaProductos = lista;
        this.onProductoAñadido = onProductoAñadido;
    }

    // Crea una nueva vista para un producto (infla el layout 'item_tienda.xml')
    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tienda, parent, false);
        return new ProductoViewHolder(itemView);
    }

    // Asocia los datos de un producto con la vista correspondiente
    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        responseProductos producto = listaProductos.get(position);

        holder.nombre.setText(producto.getNombre());
        holder.precio.setText(producto.getPrecio() + "€");

        // --- INICIO DE LA LÓGICA DE CARGA DE IMAGEN CON DEBUGGING ---
        String imageUrl = producto.getImagen(); // Obtiene la URL de la imagen del producto

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d(TAG, "Attempting to load image for: " + producto.getNombre() + " from URL: " + imageUrl);
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.logopro) // Imagen de placeholder mientras carga
                    .error(R.drawable.logopro)       // Imagen de error si la carga falla
                    .into(holder.imagen, new Callback() { // Añadimos el Callback aquí
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Image loaded successfully for: " + producto.getNombre());
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Error loading image for: " + producto.getNombre() + " from URL: " + imageUrl, e);
                            // Picasso ya mostrará la imagen de error definida (.error(R.drawable.logopro))
                        }
                    });
        } else {
            // Si la URL es nula o vacía, muestra la imagen por defecto y logea una advertencia
            Log.w(TAG, "Image URL is null or empty for product: " + producto.getNombre() + ". Displaying default image.");
            holder.imagen.setImageResource(R.drawable.logopro); // Asegúrate de que esta es la imagen por defecto deseada
        }
        // --- FIN DE LA LÓGICA DE CARGA DE IMAGEN CON DEBUGGING ---


        holder.btnAgregar.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Botón agregar clickeado para producto: " + producto.getNombre());
            // AQUI ESTA LA MODIFICACIÓN: Añadir producto.getId() como primer argumento
            responseCarrito itemCarrito = new responseCarrito(
                    producto.getId(), // <-- AÑADIDO: El ID del producto
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    1, // Cantidad inicial
                    producto.getImagen(),
                    responseCarrito.Tipo.PRODUCTO
            );

            boolean encontrado = false;
            for (responseCarrito item : responseCarrito.carritoItemsGlobal) {
                // Modificado para comparar por ID y Tipo
                if (item.getId() == itemCarrito.getId() && item.getTipo() == itemCarrito.getTipo()) {
                    item.setCantidad(item.getCantidad() + 1);
                    Log.d(TAG, "onClick: Producto ya en carrito, incrementando cantidad para: " + item.getNombre() + ". Nueva cantidad: " + item.getCantidad());
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                responseCarrito.carritoItemsGlobal.add(itemCarrito);
                Log.d(TAG, "onClick: Nuevo producto añadido al carrito global: " + itemCarrito.getNombre());
            }

            if (onProductoAñadido != null) {
                onProductoAñadido.run();
            }
            Toast.makeText(v.getContext(), "Producto añadido: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onClick: Toast mostrado para producto: " + producto.getNombre());
        });

        // Botón detalles
        holder.btnDetalles.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            View vistaDetalle = inflater.inflate(R.layout.dialogo_detalle_producto, null);

            ImageView imagenDetalle = vistaDetalle.findViewById(R.id.imagen_detalle);
            TextView nombreDetalle = vistaDetalle.findViewById(R.id.nombre_detalle);
            TextView descripcionDetalle = vistaDetalle.findViewById(R.id.descripcion_detalle);
            TextView precioDetalle = vistaDetalle.findViewById(R.id.precio_detalle);
            TextView graduacionDetalle = vistaDetalle.findViewById(R.id.graduacion_detalle);
            TextView categoriaDetalle = vistaDetalle.findViewById(R.id.categoria_detalle);
            // Asegúrate de que este TextView existe en tu dialog_detalle_producto.xml



            // --- LÓGICA DE CARGA DE IMAGEN EN DIÁLOGO CON DEBUGGING ---
            String imageUrlDetalle = producto.getImagen();
            if (imageUrlDetalle != null && !imageUrlDetalle.isEmpty()) {
                Log.d(TAG, "Attempting to load detail image for: " + producto.getNombre() + " from URL: " + imageUrlDetalle);
                Picasso.get().load(imageUrlDetalle)
                        .placeholder(R.drawable.logopro)
                        .error(R.drawable.logopro)
                        .into(imagenDetalle, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Detail image loaded successfully for: " + producto.getNombre());
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "Error loading detail image for: " + producto.getNombre() + " (" + imageUrlDetalle + "): " + e.getMessage(), e);
                            }
                        });
            } else {
                Log.w(TAG, "Detail image URL is null or empty for product: " + producto.getNombre() + ". Displaying default image.");
                imagenDetalle.setImageResource(R.drawable.logopro);
            }
            // --- FIN LÓGICA DE CARGA DE IMAGEN EN DIÁLOGO CON DEBUGGING ---

            nombreDetalle.setText(producto.getNombre());
            descripcionDetalle.setText("Descripción: " + producto.getDescripcion());
            precioDetalle.setText("Precio: " + String.format("%.2f€", producto.getPrecio()));
            graduacionDetalle.setText("Graduación: " + producto.getGraduacion() + "%");
            // <-- AÑADIDO: Mostrar el stock si el TextView stockDetalle existe y getCantidad() es el stock



            // LÓGICA CLAVE: Obtener el nombre de la categoría usando el mapa
            String categoryName = categoryNames.get(producto.getIdCategoria());
            if (categoryName != null) {
                categoriaDetalle.setText("Categoría: " + categoryName);
                Log.d(TAG, "Categoría mostrada: " + categoryName + " para ID: " + producto.getId());
            } else {
                // Manejo de IDs desconocidos y log de advertencia
                categoriaDetalle.setText("Categoría: Desconocida (ID: " + producto.getIdCategoria() + ")");
                Log.w(TAG, "ID de categoría desconocido: " + producto.getIdCategoria() + " para producto ID: " + producto.getId());
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setView(vistaDetalle);
            builder.setPositiveButton("Cerrar", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, precio;
        ImageView imagen;
        Button btnAgregar, btnDetalles;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre_articulo);
            precio = itemView.findViewById(R.id.precio_articulo);
            imagen = itemView.findViewById(R.id.imagen_articulo);
            btnAgregar = itemView.findViewById(R.id.boton_agregar_carrito);
            btnDetalles = itemView.findViewById(R.id.boton_detalles);
        }
    }
}