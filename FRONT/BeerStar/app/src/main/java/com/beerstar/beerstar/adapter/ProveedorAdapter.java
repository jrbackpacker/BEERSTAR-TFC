package com.beerstar.beerstar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.response.responseProveedores; // Kept Spanish name for consistency
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter for RecyclerView that displays a list of providers.
 * Each provider contains name, address, phone, and logo.
 */
public class ProveedorAdapter extends RecyclerView.Adapter<ProveedorAdapter.ProveedorViewHolder> { // Kept 'ProveedorAdapter' as class name

    // List of provider objects to be displayed
    private List<responseProveedores> providers; // Changed from 'proveedores' to 'providers'

    // Constructor that receives the list to be displayed
    public ProveedorAdapter(List<responseProveedores> providers) { // Changed variable name
        this.providers = providers; // Changed variable name
    }

    /**
     * Inflates the XML layout for each item (row) of the RecyclerView.
     * This method is called when a new view needs to be created.
     */
    @NonNull
    @Override
    public ProveedorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom provider view (item_proveedor.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proveedor, parent, false); // R.layout will remain the same
        return new ProveedorViewHolder(view);
    }

    /**
     * Associates the data of a provider with the views of a ViewHolder.
     * This method is called for each visible item in the RecyclerView.
     */
    @Override
    public void onBindViewHolder(@NonNull ProveedorViewHolder holder, int position) {
        responseProveedores provider = providers.get(position); // Changed variable name

        // Associate provider data with ViewHolder views
        holder.providerName.setText(provider.getName()); // Changed from nombreProveedor to providerName, and getName()
        holder.providerDescription.setText(provider.getAddress()); // Changed from descripcionProveedor to providerDescription, and getAddress()
        holder.providerContact.setText("Tel: " + provider.getPhone() + "\nEmail: " + provider.getEmail()); // Changed variables and added email for display

        // If there is an image URL, load it using Picasso
        if (provider.getLogoUrl() != null && !provider.getLogoUrl().isEmpty()) { // Changed from getImagen() to getLogoUrl()
            Picasso.get().load(provider.getLogoUrl()).into(holder.providerLogo); // Changed from getImagen() to getLogoUrl(), and logoProveedor to providerLogo
        } else {
            holder.providerLogo.setImageResource(R.drawable.borrar); // You can have a default image
        }
    }

    /**
     * Returns the total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return providers.size(); // Changed variable name
    }

    /**
     * Custom ViewHolder to hold references to the views
     * of each RecyclerView item, avoiding unnecessary findViewById calls.
     */
    public static class ProveedorViewHolder extends RecyclerView.ViewHolder { // Kept 'ProveedorViewHolder' as class name
        TextView providerName; // Changed from nombreProveedor to providerName
        TextView providerDescription; // Changed from descripcionProveedor to providerDescription
        TextView providerContact; // Changed from contactoProveedor to providerContact
        ImageView providerLogo; // Changed from logoProveedor to providerLogo

        public ProveedorViewHolder(@NonNull View itemView) {
            super(itemView);
            // Associate views with layout elements
            providerName = itemView.findViewById(R.id.nombre_proveedor); // R.id will remain the same as it refers to XML ID
            providerDescription = itemView.findViewById(R.id.descripcion_proveedor); // R.id will remain the same
            providerContact = itemView.findViewById(R.id.contacto_proveedor); // R.id will remain the same
            providerLogo = itemView.findViewById(R.id.logo_proveedor); // R.id will remain the same
        }
    }
}