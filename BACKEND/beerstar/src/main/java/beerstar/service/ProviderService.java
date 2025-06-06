package beerstar.service;

import beerstar.dto.ProviderDTO;

import java.util.List;
import java.util.Optional;

public interface ProviderService {
    List<ProviderDTO> getAllProviders();
    Optional<ProviderDTO> getProviderById(Long id);
    // Métodos para crear, actualizar, eliminar si son necesarios,
    // pero para "listar" nos centramos en los GET
}