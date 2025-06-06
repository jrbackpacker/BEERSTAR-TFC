package com.beerstar.beerstar.response;

import com.google.gson.annotations.SerializedName;
// No se importan TypeAdapter, JsonDeserializer, etc. porque solo se traduce, no se añade lógica de fecha.

public class responseProveedores {

    // Keep as id, as Spring's ProviderDTO has 'id'
    private int id; // Corresponds to 'id' in ProviderDTO
    private String name; // Changed from 'nombre' to 'name'
    private String address; // Changed from 'direccion' to 'address'
    private String phone; // Changed from 'telefono' to 'phone'
    private String email; // Added email, as ProviderDTO has it
    // IMPORTANT: If registration_date comes as an array [year, month, day, ...], this `String`
    // will *not* automatically deserialize it. You will need a custom Gson TypeAdapter
    // or change the backend to send it as a formatted String.
    private String registrationDate; // Changed from 'fechaRegistro' to 'registrationDate'

    // @SerializedName is no longer needed here if your DTO also uses "logoUrl"
    // However, if Android client refers to it as 'image', you can keep it:
    // @SerializedName("logoUrl") // Ensures it maps to "logoUrl" from Spring
    private String logoUrl; // Changed from 'imagen' to 'logoUrl' to match backend JSON field directly

    // Make sure you have all the fields you receive from the server.
    // Remove the inner 'Usuario' class if the server does not send a nested 'usuario' object.

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; } // Changed from getNombre to getName
    public void setName(String name) { this.name = name; } // Changed from setNombre to setName

    public String getAddress() { return address; } // Changed from getDireccion to getAddress
    public void setAddress(String address) { this.address = address; } // Changed from setDireccion to setAddress

    public String getPhone() { return phone; } // Changed from getTelefono to getPhone
    public void setPhone(String phone) { this.phone = phone; } // Changed from setTelefono to setPhone

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRegistrationDate() { return registrationDate; } // Changed from getFechaRegistro to getRegistrationDate
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; } // Changed from setFechaRegistro to setRegistrationDate

    public String getLogoUrl() { return logoUrl; } // Changed from getImagen to getLogoUrl
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; } // Changed from setImagen to setLogoUrl
}