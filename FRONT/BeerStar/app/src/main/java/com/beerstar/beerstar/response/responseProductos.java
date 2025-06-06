package com.beerstar.beerstar.response;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class responseProductos {

    // Lista global del carrito, usada para almacenar los productos añadidos al carrito
    public static List<responseProductos> carritoGlobal = new ArrayList<>();

    // Atributos del producto
    private int id;                        // Identificador único del producto
    private String nombre;                  // Nombre del producto
    private String descripcion;             // Descripción del producto
    private double precio;                  // Precio del producto

    // ¡¡¡CAMBIO CRÍTICO AQUÍ!!!
    // Ya no necesitas @SerializedName("url") porque el backend envía "imagen"
    private String imagen;                  // URL de la imagen del producto

    private int cantidad;                   // Cantidad de producto disponible
    private double graduacion;              // Graduación alcohólica del producto (si aplica)
    private int idCategoria;                // ID de la categoría a la que pertenece el producto
    private String nombreCategoria;         // Nombre de la categoría del producto

    // Constructor de la clase para inicializar los atributos
    public responseProductos(int id, String nombre, String descripcion, double precio,
                             int cantidad, double graduacion, int idCategoria,
                             String nombreCategoria, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagenUrl;  // Asigna la URL de la imagen
        this.cantidad = cantidad;
        this.graduacion = graduacion;
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
    }

    // Métodos getter y setter para acceder y modificar los atributos del producto

    public int getId() {
        return id;  // Devuelve el ID del producto
    }

    public void setId(int id) {
        this.id = id;  // Establece un nuevo ID para el producto
    }

    public String getNombre() {
        return nombre;  // Devuelve el nombre del producto
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;  // Establece un nuevo nombre para el producto
    }

    public String getDescripcion() {
        return descripcion;  // Devuelve la descripción del producto
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;  // Establece una nueva descripción para el producto
    }

    public double getPrecio() {
        return precio;  // Devuelve el precio del producto
    }

    public void setPrecio(double precio) {
        this.precio = precio;  // Establece un nuevo precio para el producto
    }

    public String getImagen() {
        return imagen;  // Devuelve la URL de la imagen del producto
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;  // Establece una nueva URL de imagen para el producto
    }

    public int getCantidad() {
        return cantidad;  // Devuelve la cantidad de producto disponible
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;  // Establece una nueva cantidad disponible para el producto
    }

    public double getGraduacion() {
        return graduacion;  // Devuelve la graduación alcohólica del producto
    }

    public void setGraduacion(double graduacion) {
        this.graduacion = graduacion;  // Establece una nueva graduación alcohólica para el producto
    }

    public int getIdCategoria() {
        return idCategoria;  // Devuelve el ID de la categoría del producto
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;  // Establece un nuevo ID de categoría para el producto
    }

    public String getNombreCategoria() {
        return nombreCategoria;  // Devuelve el nombre de la categoría del producto
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;  // Establece un nuevo nombre para la categoría del producto
    }
}