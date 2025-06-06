// beerstar/dto/BatchDTO.java
package beerstar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDTO {
    private Long id;
    private String nombre;
    private int stock;
    private String url;
    private String descripcion;
    private double precio;

    // Nuevo: Información del proveedor
    private Long providerId;
    private String providerName; // Para mostrar el nombre del proveedor en el frontend


}