package beerstar.dto;

import lombok.Data;
import java.util.List; // Importar List

@Data
public class UserDTO {
    private Long id;
    private String email;
    String password; // Comentar o eliminar para no exponer la contraseña en las respuestas
    private String nombre;
    private String telefono;
    private String direccion;
    private List<String> roles; // Para enviar los roles del usuario
}