package beerstar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    @Id // Este ID se mapeará desde el ID del User

    private Long id; // Este campo 'id' tomará el mismo valor que el 'id' del User asociado.

    @MapsId // Indica que la clave primaria de esta entidad es también una clave foránea a otra entidad
    @OneToOne(fetch = FetchType.LAZY) // Relación uno a uno con la entidad User
    @JoinColumn(name = "id") // El nombre de la columna FK en la tabla 'providers' que apunta a 'users.id'
    private User user; // La entidad User asociada

    private String name; // Nombre del proveedor
    private String phone;
    private String address;
    private String logoUrl;
    private LocalDateTime fechaRegistro;


}