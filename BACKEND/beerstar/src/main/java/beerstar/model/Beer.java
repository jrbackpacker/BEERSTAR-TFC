// beerstar/model/Beer.java
package beerstar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Beer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private User provider;

    private Double graduacion;
    private int stock;
    private String imagen;
    private String descripcion;
    private double precio;

    private Long idCategoria;
    private String nombreCategoria;

    // Métodos de conveniencia
    public Long getProviderId() {
        return (provider != null) ? provider.getId() : null;
    }
}