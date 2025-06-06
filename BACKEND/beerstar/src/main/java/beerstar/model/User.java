package beerstar.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Entity
@Table(name = "users") // Nombre de tu tabla de usuarios
public class User { // O User

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(unique = true, nullable = false)
    private String email;

    @Getter // Asegúrate de tener el getter para password si lo usas fuera de seguridad
    @Column(nullable = false)
    private String password;

    @Getter
    private String nombre;
    @Getter
    private String telefono;
    @Getter
    private String direccion;

    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String email, String password, String nombre, String telefono, String direccion) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }


}