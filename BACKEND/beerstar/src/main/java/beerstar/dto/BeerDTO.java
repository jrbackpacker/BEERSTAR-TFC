// beerstar/dto/BeerDTO.java
package beerstar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerDTO {
    private Long id;
    private String nombre;
    private Double graduacion;
    private int stock;
    private String imagen;
    private String descripcion;
    private double precio;
    private Long idCategoria;
    private String nombreCategoria;

    // Nuevo: Información del proveedor
    private Long providerId;
    private String providerName; // Para mostrar el nombre del proveedor en el frontend
}