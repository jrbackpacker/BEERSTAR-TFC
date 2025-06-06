package beerstar.repository;

import beerstar.model.Role; // Asegúrate de que esta ruta sea correcta
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // Método para buscar un rol por su nombre
}