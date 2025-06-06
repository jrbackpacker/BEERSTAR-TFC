package com.beerstar.beerstar.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class responseUsuario implements Serializable {

    // Campos presentes en respuestas de error o éxito, depende del backend
    private boolean success;
    private String mensaje;

    // Campos presentes en la respuesta de login exitoso (según tu Postman + necesidades de perfil)
    private String token;

    @SerializedName("idUsuario") // Mapea "id" del JSON a "idUsuario"
    private long idUsuario;

    private String email;
    private String tipoUsuario; // Ej: "CLIENTE", "PROVEEDOR"

    @SerializedName("roles") // Mapea el array "roles" del JSON
    private List<String> listaRoles; // Lista de roles (ej: "ROLE_USER")

    private String type; // Ej: "Bearer"

    // *** Campos de perfil que quieres guardar/mostrar/actualizar ***
    private String nombre;
    private String telefono;
    private String direccion;


    // Constructor vacío necesario para Retrofit/Gson
    public responseUsuario() {
    }

    // Constructor completo (principalmente para uso interno o mocks)
    public responseUsuario(boolean success, String mensaje, String token, long idUsuario, String email, String tipoUsuario, List<String> listaRoles, String type, String nombre, String telefono, String direccion) {
        this.success = success;
        this.mensaje = mensaje;
        this.token = token;
        this.idUsuario = idUsuario;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.listaRoles = listaRoles;
        this.type = type;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // --- Getters (asegúrate de que estén todos) ---
    public boolean isSuccess() { return success; }
    public String getMensaje() { return mensaje; }
    public String getToken() { return token; }
    public long getIdUsuario() { return idUsuario; }
    public String getEmail() { return email; }
    public String getTipoUsuario() { return tipoUsuario; }
    public List<String> getListaRoles() { return listaRoles; }
    public String getType() { return type; }

    // *** Getters para campos de perfil ***
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }

    // --- Setters (necesarios para SessionManager si usas el constructor vacío) ---
    public void setSuccess(boolean success) { this.success = success; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setToken(String token) { this.token = token; }
    public void setIdUsuario(long idUsuario) { this.idUsuario = idUsuario; }
    public void setEmail(String email) { this.email = email; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    public void setListaRoles(List<String> listaRoles) { this.listaRoles = listaRoles; }
    public void setType(String type) { this.type = type; }

    // *** Setters para campos de perfil ***
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    // Opcional: Puedes sobrescribir toString() para facilitar la depuración
    @Override
    public String toString() {
        return "responseUsuario{" +
                "success=" + success +
                ", mensaje='" + mensaje + '\'' +
                ", token='" + (token != null ? token.substring(0, Math.min(token.length(), 10)) + "..." : "null") + '\'' + // Evitar imprimir token completo
                ", idUsuario=" + idUsuario +
                ", email='" + email + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", listaRoles=" + listaRoles +
                ", type='" + type + '\'' +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}