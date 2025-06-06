package com.beerstar.beerstar.response;

import java.io.Serializable; // Añadir si no está, ya que se usa en Bundle
import java.util.ArrayList;
import java.util.List;

public class responseCarrito implements Serializable {

    // Enum que define los dos tipos posibles: PRODUCTO o LOTE
    public enum Tipo {
        PRODUCTO, LOTE
    }

    // Lista global del carrito, usada para almacenar tanto productos como lotes
    public static List<responseCarrito> carritoItemsGlobal = new ArrayList<>();

    private int id;                // <-- AÑADIDO: ID del producto o lote original
    private String nombre;         // Nombre del producto o lote
    private String descripcion;    // Descripción del producto o lote
    private double precio;         // Precio del producto o lote
    private int cantidad;          // Cantidad de unidades en el carrito
    private String imagenUrl;      // URL de la imagen del producto o lote
    private Tipo tipo;             // Tipo que puede ser producto o lote

    // Constructor que inicializa todos los valores del carrito
    // AÑADIDO: id en el constructor
    public responseCarrito(int id, String nombre, String descripcion, double precio, int cantidad, String imagenUrl, Tipo tipo) {
        this.id = id; // Asignar el ID
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagenUrl = imagenUrl;
        this.tipo = tipo;
    }

    // Constructor vacío (necesario para la deserialización en algunos casos)
    public responseCarrito() {
    }

    // Métodos getter para obtener los valores de las propiedades

    public int getId() { // <-- AÑADIDO: Getter para el ID
        return id;
    }

    public void setId(int id) { // <-- AÑADIDO: Setter para el ID
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public Tipo getTipo() {
        return tipo;
    }

    // Método setter para actualizar la cantidad en el carrito
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}