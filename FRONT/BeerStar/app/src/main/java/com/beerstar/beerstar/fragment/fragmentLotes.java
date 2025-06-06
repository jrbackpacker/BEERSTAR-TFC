package com.beerstar.beerstar.fragment;

import android.os.Bundle;
import android.util.Log; // Importar la clase Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.beerstar.beerstar.R;
import com.beerstar.beerstar.adapter.LotesAdapter;
import com.beerstar.beerstar.service.RetrofitClient;
import com.beerstar.beerstar.service.ApiService;
import com.beerstar.beerstar.response.responseLotes;
import com.beerstar.beerstar.response.responseCarrito;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragmentLotes extends Fragment {

    // Declara una referencia al RecyclerView y su adaptador
    private RecyclerView recyclerView;
    private LotesAdapter lotesAdapter;

    private static final String TAG = "LotesLog"; // Etiqueta para los logs

    // Método que se llama al crear la vista del fragmento
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando fragmentLotes");
        View view = inflater.inflate(R.layout.fragment_lotes, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        obtenerLotesDesdeAPI();

        return view;
    }

    // Método para obtener la lista de lotes desde la API
    private void obtenerLotesDesdeAPI() {
        Log.d(TAG, "obtenerLotesDesdeAPI: Realizando llamada a la API para obtener lotes");
        ApiService apiService = RetrofitClient.getApiService();

        Call<List<responseLotes>> call = apiService.getLotes();
        call.enqueue(new Callback<List<responseLotes>>() {
            @Override
            public void onResponse(Call<List<responseLotes>> call, Response<List<responseLotes>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Lotes obtenidos exitosamente. Cantidad: " + response.body().size());
                    // Pasa la lista de lotes obtenida y el listener al adaptador
                    lotesAdapter = new LotesAdapter(response.body(), lote -> añadirAlCarrito(lote));
                    recyclerView.setAdapter(lotesAdapter);
                } else {
                    Log.e(TAG, "onResponse: Error en la respuesta de la API. Código: " + response.code());
                    Toast.makeText(getContext(), "Error en la respuesta de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<responseLotes>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error de conexión al obtener lotes", t);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Método para añadir un lote al carrito
    private void añadirAlCarrito(responseLotes lote) {
        Log.d(TAG, "añadirAlCarrito: Intentando añadir lote con ID: " + lote.getId() + ", Nombre: " + lote.getNombre());

        // AQUI ESTA LA MODIFICACIÓN: Añadir lote.getId() como primer argumento
        responseCarrito itemCarrito = new responseCarrito(
                lote.getId(), // <-- AÑADIDO: El ID del lote
                lote.getNombre(),
                lote.getDescripcion(),
                lote.getPrecio(),
                1, // Cantidad inicial del lote en el carrito
                lote.getImagenUrl(),
                responseCarrito.Tipo.LOTE
        );

        // Verifica si el lote ya está en el carrito
        boolean encontrado = false;
        for (responseCarrito item : responseCarrito.carritoItemsGlobal) {
            // Asegúrate de que los nombres no sean nulos antes de comparar
            // Y compara por ID y Tipo para identificar el item único
            if (item.getId() == itemCarrito.getId() && item.getTipo() == itemCarrito.getTipo()) {
                item.setCantidad(item.getCantidad() + 1);
                Log.d(TAG, "añadirAlCarrito: Lote ya en carrito, incrementando cantidad para: " + item.getNombre() + ". Nueva cantidad: " + item.getCantidad());
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            responseCarrito.carritoItemsGlobal.add(itemCarrito);
            Log.d(TAG, "añadirAlCarrito: Nuevo lote añadido al carrito global: " + (itemCarrito.getNombre() != null ? itemCarrito.getNombre() : "Nombre Nulo"));
        }

        Toast.makeText(getContext(), lote.getNombre() + " añadido al carrito!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "añadirAlCarrito: Toast mostrado para lote: " + (lote.getNombre() != null ? lote.getNombre() : "Nombre Nulo"));
    }
}