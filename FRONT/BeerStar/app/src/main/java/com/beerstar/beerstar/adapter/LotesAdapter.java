// Paquete al que pertenece este adaptador
package com.beerstar.beerstar.adapter;

// Importaciones necesarias para vistas y componentes
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log; // Importar Log para depuración

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.response.responseLotes; // Asegúrate de que este import apunta a tu responseLotes modificado
import com.squareup.picasso.Picasso;

import java.util.List;

// Adaptador para mostrar una lista de lotes en un RecyclerView
public class LotesAdapter extends RecyclerView.Adapter<LotesAdapter.LoteViewHolder> {

    // Lista que contiene los lotes que se van a mostrar
    private List<responseLotes> lotesList;

    // Listener que se ejecuta cuando el usuario hace clic en añadir al carrito para un lote
    private OnAñadirCarritoClickListener listener;

    private static final String TAG = "LotesAdapterLog"; // Etiqueta para los logs

    // Interfaz para manejar clics sobre el botón de añadir al carrito de los lotes
    public interface OnAñadirCarritoClickListener {
        void onAñadirClick(responseLotes lote); // Método que se llama al hacer clic en el botón de añadir
    }

    // Constructor del adaptador, recibe la lista de lotes y el listener
    public LotesAdapter(List<responseLotes> lotesList, OnAñadirCarritoClickListener listener) {
        this.lotesList = lotesList;
        this.listener = listener;
        Log.d(TAG, "LotesAdapter creado con " + lotesList.size() + " lotes.");
    }

    // Método que crea las vistas de cada item del RecyclerView (infla item_lote.xml)
    @NonNull
    @Override
    public LoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Inflando item_lote.xml");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lote, parent, false);
        return new LoteViewHolder(view);
    }

    // Método que asigna datos a cada elemento visual según su posición
    @Override
    public void onBindViewHolder(@NonNull LoteViewHolder holder, int position) {
        responseLotes lote = lotesList.get(position);
        // *** CAMBIO AQUÍ: Usar getNombre() en lugar de getMarca() ***
        Log.d(TAG, "onBindViewHolder: Enlazando datos para lote en posición " + position + ": " + (lote.getNombre() != null ? lote.getNombre() : "Nombre Nulo"));

        holder.nombreTextView.setText(lote.getNombre()); // *** CAMBIO AQUÍ: Usar getNombre() ***
        holder.descripcionTextView.setText(lote.getDescripcion());
        holder.precioTextView.setText("Precio: " + lote.getPrecio() + " €");

        // *** CAMBIO AQUÍ: Usar getImagenUrl() en lugar de getImagen_url() ***
        String imagenUrl = lote.getImagenUrl();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Picasso.get()
                    .load(imagenUrl)
                    .placeholder(R.drawable.logopro)
                    .error(R.drawable.logopro)
                    .into(holder.imagenLote);
            Log.d(TAG, "onBindViewHolder: Cargando imagen desde URL: " + imagenUrl);
        } else {
            // Si la URL es nula o vacía, muestra la imagen placeholder
            holder.imagenLote.setImageResource(R.drawable.logopro);
            Log.w(TAG, "onBindViewHolder: URL de imagen nula o vacía para lote: " + lote.getNombre());
        }


        // Asegúrate de que R.id.boton_agregar_carrito_lote es el ID correcto de tu botón en item_lote.xml
        if (holder.botonAgregarCarrito != null) {
            holder.botonAgregarCarrito.setOnClickListener(v -> {
                // *** CAMBIO AQUÍ: Usar getNombre() en el log ***
                Log.d(TAG, "onClick: Botón añadir carrito clickeado para lote: " + (lote.getNombre() != null ? lote.getNombre() : "Nombre Nulo"));
                if (listener != null) {
                    listener.onAñadirClick(lote); // Llamar al método del listener
                }
            });
        } else {
            Log.e(TAG, "onBindViewHolder: Botón agregar carrito (ID R.id.boton_agregar_carrito_lote) es null. Verifica item_lote.xml");
        }

        // Elimina o comenta la línea siguiente si solo el botón debe añadir al carrito
        // holder.itemView.setOnClickListener(v -> listener.onAñadirClick(lote));
    }

    @Override
    public int getItemCount() {
        return lotesList.size();
    }

    // Clase interna que representa cada fila individual en el RecyclerView
    public static class LoteViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView, descripcionTextView, precioTextView;
        ImageView imagenLote;
        Button botonAgregarCarrito; // Referencia al botón de añadir al carrito

        public LoteViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView); // Corregido possible typo aquí
            precioTextView = itemView.findViewById(R.id.precioTextView);
            imagenLote = itemView.findViewById(R.id.imagen_lote);
            // Asegúrate de usar el ID correcto de tu botón en item_lote.xml
            botonAgregarCarrito = itemView.findViewById(R.id.boton_agregar_carrito_lote);
        }
    }
}