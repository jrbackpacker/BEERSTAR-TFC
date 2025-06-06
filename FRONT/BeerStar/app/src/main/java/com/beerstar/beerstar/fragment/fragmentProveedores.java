package com.beerstar.beerstar.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.adapter.ProveedorAdapter; // Kept the Spanish name 'ProveedorAdapter' for consistency if not refactoring whole project
import com.beerstar.beerstar.response.responseProveedores; // Kept the Spanish name 'responseProveedores'
import com.beerstar.beerstar.service.ApiService;
import com.beerstar.beerstar.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragmentProveedores extends Fragment {

    // Declare RecyclerView, adapter and list of providers
    private RecyclerView recyclerView;
    private ProveedorAdapter providerAdapter; // Changed from 'proveedorAdapter' to 'providerAdapter' for English consistency
    private List<responseProveedores> providers = new ArrayList<>(); // Changed from 'proveedores' to 'providers'
    private static final String TAG = "fragmentProviders"; // Tag for logs (Changed from 'fragmentProveedores' to 'fragmentProviders')

    // Empty constructor for the fragment
    public fragmentProveedores() {
    }

    // Method called when the fragment's view is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment's view from the XML file
        View view = inflater.inflate(R.layout.fragment_proveedores, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewProveedores); // R.id will remain the same
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create an adapter with the list of providers and assign it to the RecyclerView
        providerAdapter = new ProveedorAdapter(providers); // Changed variable name
        recyclerView.setAdapter(providerAdapter); // Changed variable name

        // Call the method to load providers from the API
        loadProviders(); // Changed method name from cargarProveedores to loadProviders

        return view;
    }

    private void loadProviders() { // Changed method name
        ApiService api = RetrofitClient.getApiService();

        api.getProveedores().enqueue(new Callback<List<responseProveedores>>() {
            @Override
            public void onResponse(Call<List<responseProveedores>> call, Response<List<responseProveedores>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    providers.clear(); // Clear the list if it already had elements (Changed variable name)
                    providers.addAll(response.body()); // Add all received providers (Changed variable name)
                    providerAdapter.notifyDataSetChanged(); // Notify the adapter (Changed variable name)
                } else {
                    Log.e(TAG, "Unsuccessful response. Code: " + response.code()); // Translated log message
                    Log.e(TAG, "Error message: " + response.message()); // Translated log message
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string()); // Translated log message
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<responseProveedores>> call, Throwable t) {
                Log.e(TAG, "Error loading providers", t); // Translated log message
            }
        });
    }

}