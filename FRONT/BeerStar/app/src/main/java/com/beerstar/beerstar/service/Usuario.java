package com.beerstar.beerstar.service;

// No se necesita importación de List para este modelo de Request
// import java.util.List; // Removido si estaba por error

public class Usuario {
    private String email;
    private String password;
    private String telefono;
    private String direccion;
    private String nombre;

    // Constructor para login y registro (con email y password)
    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor vacío necesario para algunos deserializadores JSON (aunque no para el request)
    public Usuario() {
    }

    // --- Getters ---
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getNombre() {
        return nombre;
    }

    // --- Setters ---
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}