// beerstar/repository/UserRepository.java
package beerstar.repository;

import beerstar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Nuevo método para encontrar usuarios por nombre de rol
    List<User> findByRoles_Name(String roleName);
}