package beerstar.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
    private String email;
    private String password;
    private String nombre;
    private String telefono;
    private String direccion;
    private String roleName; // <--- ¡AÑADE ESTA LÍNEA!
}