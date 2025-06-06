package com.beerstar.beerstar.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log; // Importar la clase Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.adapter.CarritoAdapter;
import com.beerstar.beerstar.response.responseCarrito;
import com.beerstar.beerstar.response.responseUsuario;
import com.beerstar.beerstar.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;


public class fragmentCarrito extends Fragment {

    private RecyclerView recyclerView;
    private CarritoAdapter carritoAdapter;
    private TextView textTotalCarrito;
    private Button botonFinalizarCompra;
    private List<responseCarrito> itemsCarrito = new ArrayList<>();
    private SessionManager sessionManager;

    private static final String TAG = "CarritoLog"; // Etiqueta para los logs


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando fragmentCarrito");
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        textTotalCarrito = view.findViewById(R.id.textTotalPrecio);
        botonFinalizarCompra = view.findViewById(R.id.botonFinalizarCompra);

        sessionManager = new SessionManager(getContext());

        // Clear the local list and populate from the global cart list
        itemsCarrito.clear();
        itemsCarrito.addAll(responseCarrito.carritoItemsGlobal);
        Log.d(TAG, "onCreateView: Carrito local poblado desde carrito global. Total items: " + itemsCarrito.size());


        carritoAdapter = new CarritoAdapter(itemsCarrito, (total, iva) -> actualizarTotal(total, iva));
        recyclerView.setAdapter(carritoAdapter);

        actualizarTotal();

        botonFinalizarCompra.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Botón Finalizar Compra clickeado");
            if (sessionManager.isLoggedIn()) {
                Log.d(TAG, "onClick: Usuario logueado");

                responseUsuario usuarioLogueado = sessionManager.getLoggedInUser();

                if (itemsCarrito.isEmpty()) {
                    Log.d(TAG, "onClick: Carrito vacío, mostrando Toast.");
                    Toast.makeText(getContext(), "Tu carrito está vacío.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuarioLogueado != null) {
                    Log.d(TAG, "onClick: Datos de usuario logueado disponibles, navegando a fragmentDetallePedido");
                    fragmentDetallePedido detalleFragment = fragmentDetallePedido.newInstance(itemsCarrito, usuarioLogueado);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, detalleFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Log.e(TAG, "onClick: Datos de usuario logueado no disponibles. Cerrando sesión y navegando a login.");
                    Toast.makeText(getContext(), "Error: Datos de usuario no disponibles.", Toast.LENGTH_SHORT).show();
                    sessionManager.logoutUser();
                    navigateToLoginFragment();
                }

            } else {
                Log.d(TAG, "onClick: Usuario no logueado, mostrando Toast y navegando a login.");
                Toast.makeText(getContext(), "Debes iniciar sesión para finalizar la compra.", Toast.LENGTH_SHORT).show();
                navigateToLoginFragment();
            }
        });

        return view;
    }


    private void navigateToLoginFragment() {
        Log.d(TAG, "navigateToLoginFragment: Navegando a fragmentUsuario (login)");
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new fragmentUsuario());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void actualizarTotal(double total, double iva) {
        Log.d(TAG, "actualizarTotal: Calculando y mostrando total. Total: " + total + ", IVA: " + iva);
        textTotalCarrito.setText("Total: " + String.format("%.2f", total) + "€" + " (IVA: " + String.format("%.2f", iva) + "€"+ " )" );
    }

    private void actualizarTotal() {
        Log.d(TAG, "actualizarTotal: Recalculando total y IVA del carrito.");
        double total = 0;
        for (responseCarrito item : itemsCarrito) {
            total += item.getPrecio() * item.getCantidad();
        }
        double iva = total * 0.21;
        actualizarTotal(total, iva);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: fragmentCarrito reanudado.");
        // Update the local list from the global cart list on resume
        itemsCarrito.clear();
        itemsCarrito.addAll(responseCarrito.carritoItemsGlobal);
        Log.d(TAG, "onResume: Carrito local actualizado desde carrito global. Total items: " + itemsCarrito.size());


        if (carritoAdapter != null) {
            Log.d(TAG, "onResume: Notificando cambios al adaptador y actualizando total.");
            carritoAdapter.notifyDataSetChanged();
            actualizarTotal();
        }
    }
}