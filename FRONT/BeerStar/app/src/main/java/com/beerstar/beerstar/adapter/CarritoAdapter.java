package com.beerstar.beerstar.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log; // Importar la clase Log


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.response.responseCarrito;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {


    private List<responseCarrito> carritoItems;


    private OnCarritoActualizadoListener listener;


    private double totalCompra = 0.0;
    private double iva = 0.0;

    private static final String TAG = "CarritoAdapterLog"; // Etiqueta para los logs


    public CarritoAdapter(List<responseCarrito> carritoItems, OnCarritoActualizadoListener listener) {
        this.carritoItems = carritoItems;
        this.listener = listener;
        calcularTotal(); // Calcular el total inicial al crear el adaptador
        Log.d(TAG, "CarritoAdapter creado con " + carritoItems.size() + " ítems.");
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Inflando item_carrito.xml");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        responseCarrito item = carritoItems.get(position);
        Log.d(TAG, "onBindViewHolder: Enlazando datos para ítem en posición " + position + ", Nombre: " + item.getNombre() + ", Tipo: " + item.getTipo());

        // *** MODIFICACIÓN CLAVE: Cargar la imagen sin importar el tipo (PRODUCTO o LOTE) ***
        String imagenUrl = item.getImagenUrl();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Picasso.get()
                    .load(imagenUrl)
                    .placeholder(R.drawable.logopro) // Placeholder mientras carga
                    .error(R.drawable.logopro)     // Imagen si hay error al cargar
                    .into(holder.imagenArticulo);
            Log.d(TAG, "onBindViewHolder: Cargando imagen en carrito para " + item.getNombre() + " desde URL: " + imagenUrl);
        } else {
            // Si la URL es nula o vacía, muestra la imagen placeholder
            holder.imagenArticulo.setImageResource(R.drawable.logopro);
            Log.w(TAG, "onBindViewHolder: URL de imagen nula o vacía para " + item.getNombre() + " en carrito. Mostrando placeholder.");
        }


        holder.nombreArticulo.setText(item.getNombre());
        holder.descripcionArticulo.setText(item.getDescripcion());
        // Formatear el precio para mostrar dos decimales
        holder.precioArticulo.setText("Precio: " + String.format("%.2f", item.getPrecio() * item.getCantidad()) + "€");
        holder.cantidadArticulo.setText("Cantidad: " + item.getCantidad());

        holder.botonRestar.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Botón Restar clickeado para " + item.getNombre());
            if (item.getCantidad() > 1) {
                item.setCantidad(item.getCantidad() - 1);
                notifyItemChanged(position); // Se actualiza visualmente el item
                calcularTotal(); // Se actualizan totales
                if (listener != null) listener.onCarritoActualizado(totalCompra, iva);
                Log.d(TAG, "onClick: Cantidad de " + item.getNombre() + " decrementada a " + item.getCantidad());
            } else {
                Log.d(TAG, "onClick: Cantidad de " + item.getNombre() + " ya es 1. No se puede restar más.");
            }
        });

        holder.botonSumar.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Botón Sumar clickeado para " + item.getNombre());
            item.setCantidad(item.getCantidad() + 1);
            notifyItemChanged(position); // Se actualiza visualmente el item
            calcularTotal(); // Se actualizan totales
            if (listener != null) listener.onCarritoActualizado(totalCompra, iva);
            Log.d(TAG, "onClick: Cantidad de " + item.getNombre() + " incrementada a " + item.getCantidad());
        });

        holder.botonEliminar.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Botón Eliminar clickeado para " + item.getNombre() + " en posición " + position);

            if (position < carritoItems.size()) { // Añadir comprobación de rango
                responseCarrito itemAEliminar = carritoItems.get(position);

                carritoItems.remove(position);
                notifyItemRemoved(position);

                notifyItemRangeChanged(position, carritoItems.size());

                Toast.makeText(v.getContext(), "Eliminado del carrito: " + itemAEliminar.getNombre(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: Ítem eliminado: " + itemAEliminar.getNombre() + ". Nueva cantidad de ítems en adaptador: " + carritoItems.size());

                calcularTotal(); // Se actualizan totales
                if (listener != null) listener.onCarritoActualizado(totalCompra, iva);
            } else {
                Log.e(TAG, "onClick: Error al eliminar ítem. Posición " + position + " fuera de rango (" + carritoItems.size() + ")");
            }


        });
    }


    @Override
    public int getItemCount() {
        return carritoItems.size();
    }

    // Método para calcular el total de la compra y el IVA
    private void calcularTotal() {
        totalCompra = 0.0;
        for (responseCarrito item : carritoItems) {
            totalCompra += item.getPrecio() * item.getCantidad();
        }
        iva = totalCompra * 0.21; // Asumiendo un IVA del 21%
        Log.d(TAG, "calcularTotal: Total calculado: " + totalCompra + ", IVA: " + iva);
    }

    // ViewHolder: clase que representa cada fila individual en el RecyclerView del carrito
    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenArticulo;
        TextView nombreArticulo, descripcionArticulo, precioArticulo, cantidadArticulo;
        Button botonRestar, botonSumar, botonEliminar;

        public CarritoViewHolder(View itemView) {
            super(itemView);
            imagenArticulo = itemView.findViewById(R.id.imagen_articulo);
            nombreArticulo = itemView.findViewById(R.id.nombre_articulo);
            descripcionArticulo = itemView.findViewById(R.id.descripcion_articulo);
            precioArticulo = itemView.findViewById(R.id.precio_articulo);
            cantidadArticulo = itemView.findViewById(R.id.cantidad_articulo);
            botonRestar = itemView.findViewById(R.id.btnRestar);
            botonSumar = itemView.findViewById(R.id.btnSumar);
            botonEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    // Interfaz para notificar a la actividad o fragmento cuando el carrito se actualiza (cambio de cantidad, eliminación)
    public interface OnCarritoActualizadoListener {
        void onCarritoActualizado(double totalCompra, double iva);
    }
}