package com.beerstar.beerstar.response;

import com.google.gson.annotations.SerializedName; // ¡Importa esta clase!

public class responseLotes {

    @SerializedName("idLote") // Mapea "idLote" del JSON al campo "id"
    private int id;                 // ID único para el lote

    @SerializedName("nombreLote") // Mapea "nombreLote" del JSON al campo "nombre"
    private String nombre;           // Nombre del lote (cambiado de 'marca')

    private double precio;          // Precio del lote

    // 'stock' no parece estar en la respuesta JSON según tu log.
    // Si la API no lo proporciona, este campo no se mapeará.
    private int stock;              // Cantidad disponible en stock

    // 'imagen_nombre' no parece estar en la respuesta JSON.
    // private String imagen_nombre;

    private String descripcion;     // Descripción del lote

    @SerializedName("url") // Mapea "url" del JSON al campo "imagenUrl"
    private String imagenUrl;       // URL de la imagen del lote (cambiado de 'url')

    // El constructor y los getters/setters deben coincidir con los nuevos nombres de campos.
    // Aquí están los getters y setters actualizados:

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() { // Cambiado de getMarca()
        return nombre;
    }

    public void setNombre(String nombre) { // Cambiado de setMarca()
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() { // Cambiado de getImagen_url()
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) { // Cambiado de setImagen_url()
        this.imagenUrl = imagenUrl;
    }
}