package beerstar.service;

import beerstar.dto.BeerDTO;
import java.util.List;
import java.util.Optional;

public interface BeerService {
    List<BeerDTO> getAllBeers();
    Optional<BeerDTO> getBeerById(Long id);
    BeerDTO createBeer(BeerDTO beerDTO);
    BeerDTO updateBeer(Long id, BeerDTO beerDTO);
    void deleteBeer(Long id);
    // NUEVO MÉTODO: Obtener cervezas por ID de proveedor
    List<BeerDTO> getBeersByProviderId(Long providerId);
}