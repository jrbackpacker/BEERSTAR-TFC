// beerstar/model/Batch.java
package beerstar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private User provider;

    private int stock;
    private String imagenUrl;
    private String descripcion;
    private double precio;

    // Métodos de conveniencia
    public Long getProviderId() {
        return (provider != null) ? provider.getId() : null;
    }
}