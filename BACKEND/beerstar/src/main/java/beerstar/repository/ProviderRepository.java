package beerstar.repository;

import beerstar.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    // Puedes añadir métodos personalizados si los necesitas
}