package com.beerstar.beerstar.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.adapter.ProductosAdapter;
import com.beerstar.beerstar.response.responseProductos;
import com.beerstar.beerstar.service.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections; // Importar Collections para ordenar
import java.util.Comparator;  // Importar Comparator para ordenación personalizada
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragmentTienda extends Fragment {

    private RecyclerView recyclerView;
    private ProductosAdapter productosAdapter;
    private List<responseProductos> listaProductos = new ArrayList<>(); // Lista original de todos los productos
    private List<responseProductos> listaProductosMostrados = new ArrayList<>(); // Lista actualmente mostrada (filtrada y ordenada)
    private Spinner spinnerFiltro;
    private EditText edtFiltroValor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTienda);
        spinnerFiltro = view.findViewById(R.id.spinnerFiltro);
        edtFiltroValor = view.findViewById(R.id.edtFiltroValor);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicialización del adaptador con la lista que se mostrará (inicialmente vacía)
        productosAdapter = new ProductosAdapter(listaProductosMostrados, () -> {
            // Esto es el callback que se ejecuta cuando se añade un producto al carrito.
            // Puedes añadir lógica aquí si necesitas actualizar algo en el fragmento Tienda
            // cuando se añade un producto (aunque usualmente la actualización es en el fragmento Carrito).
            // Por ahora, lo dejamos vacío como estaba.
        });
        recyclerView.setAdapter(productosAdapter);

        // Creación y configuración del adaptador para el Spinner (filtros/ordenación)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.filtros_tienda, // *** USAR EL NUEVO ARRAY DE STRINGS ***
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);

        // Listener para el Spinner: aplicar filtro/ordenación cuando cambia la selección
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltroOrdenacion(); // Llamar al método actualizado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // TextWatcher para el EditText: aplicar filtro/ordenación cuando cambia el texto
        edtFiltroValor.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltroOrdenacion(); // Llamar al método actualizado
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Obtener los productos desde la API al inicio
        obtenerProductos();

        return view;
    }

    // Método para obtener los productos desde la API
    private void obtenerProductos() {
        RetrofitClient.getApiService().obtenerProductos().enqueue(new Callback<List<responseProductos>>() {
            @Override
            public void onResponse(@NonNull Call<List<responseProductos>> call, @NonNull Response<List<responseProductos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProductos.clear();
                    listaProductos.addAll(response.body());
                    aplicarFiltroOrdenacion(); // Aplicar filtro/ordenación inicial con los datos obtenidos
                } else {
                    Toast.makeText(getContext(), "Error al obtener productos: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<responseProductos>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error de red al obtener productos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método actualizado para aplicar filtro y ordenación a la lista de productos
    private void aplicarFiltroOrdenacion() {
        String textoFiltro = edtFiltroValor.getText().toString().trim().toLowerCase();
        String tipoSeleccionado = spinnerFiltro.getSelectedItem().toString(); // Obtener el texto seleccionado del Spinner

        // 1. FILTRAR la lista original (listaProductos)
        List<responseProductos> listaTemporalFiltrada = new ArrayList<>();
        for (responseProductos producto : listaProductos) {
            boolean coincideFiltro = false;

            // Lógica de filtrado basada en el texto del EditText y la opción del Spinner
            if (tipoSeleccionado.equals("Categoría")) {
                // Si el Spinner es "Categoría", filtrar por nombre de categoría
                if (producto.getNombreCategoria() != null) {
                    coincideFiltro = producto.getNombreCategoria().toLowerCase().contains(textoFiltro);
                }
            } else {
                // Si el Spinner no es "Categoría", filtrar por nombre del producto
                if (producto.getNombre() != null) {
                    coincideFiltro = producto.getNombre().toLowerCase().contains(textoFiltro);
                }
            }

            // Añadir el producto a la lista temporal si coincide con el filtro
            if (coincideFiltro) {
                listaTemporalFiltrada.add(producto);
            }
        }

        // 2. ORDENAR la lista temporal filtrada (listaTemporalFiltrada)
        switch (tipoSeleccionado) {
            case "Nombre (A-Z)":
                Collections.sort(listaTemporalFiltrada, Comparator.comparing(responseProductos::getNombre));
                break;
            case "Nombre (Z-A)":
                Collections.sort(listaTemporalFiltrada, Comparator.comparing(responseProductos::getNombre).reversed());
                break;
            case "Precio (Más bajo)":
                Collections.sort(listaTemporalFiltrada, Comparator.comparingDouble(responseProductos::getPrecio));
                break;
            case "Precio (Más alto)":
                Collections.sort(listaTemporalFiltrada, Comparator.comparingDouble(responseProductos::getPrecio).reversed());
                break;
            case "Sin ordenar":
            case "Categoría":
                // Si es "Sin ordenar" o "Categoría", no aplicar ordenación adicional,
                // mantener el orden en que quedaron después del filtrado.
                // Opcionalmente, podrías ordenar por nombre A-Z por defecto en estos casos.
                // Collections.sort(listaTemporalFiltrada, Comparator.comparing(responseProductos::getNombre));
                break;
        }

        // 3. Actualizar la lista mostrada (listaProductosMostrados) y notificar al adaptador
        listaProductosMostrados.clear();
        listaProductosMostrados.addAll(listaTemporalFiltrada);

        productosAdapter.notifyDataSetChanged();

        // Opcional: Mostrar un mensaje si no hay resultados después del filtro
        if (listaProductosMostrados.isEmpty() && !textoFiltro.isEmpty()) {
            // Toast.makeText(getContext(), "No se encontraron productos para '" + textoFiltro + "'", Toast.LENGTH_SHORT).show();
            // Este Toast puede ser molesto con cada letra escrita, considera mostrar un TextView en su lugar.
        }
    }
}