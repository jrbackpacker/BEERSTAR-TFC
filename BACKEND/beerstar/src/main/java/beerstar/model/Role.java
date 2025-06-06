package beerstar.model;

import jakarta.persistence.*; // Usa jakarta.persistence si estás en Spring Boot 3+
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles") // Nombre de tu tabla de roles
public class Role {

    // --- Getters y Setters ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Ej: "SUPERUSER", "ADMIN", "PROVIDER", "CLIENT"

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

}