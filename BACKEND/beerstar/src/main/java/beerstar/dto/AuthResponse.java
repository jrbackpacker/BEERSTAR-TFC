// beerstar/dto/AuthResponse.java
package beerstar.dto;

import lombok.Data;
import java.util.List;

@Data
public class AuthResponse {
    private boolean success;
    private String mensaje;
    private String token;
    private Long idUsuario; // Coincide con idUsuario en Android (Este es el ID del User, que puede ser proveedor)
    private String email;
    private String tipoUsuario; // CLIENTE, PROVEEDOR, etc. (Usarás esto para identificar el rol principal)
    private List<String> listaRoles; // Coincide con listaRoles en Android (Para roles detallados, ej: ["ROLE_PROVIDER"])
    private String type; // Ej: "Bearer"

    // Campos de perfil para devolver
    private String nombre;
    private String telefono;
    private String direccion;
}