package beerstar.repository;

import beerstar.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {
    // Corrected method to find beers by provider's ID
    List<Beer> findByProvider_Id(Long providerId); // Note the underscore '_'
}