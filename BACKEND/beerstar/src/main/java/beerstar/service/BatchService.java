package beerstar.service;

import beerstar.dto.BatchDTO;

import java.util.List;
import java.util.Optional;

public interface BatchService {
    List<BatchDTO> getAllBatches();
    Optional<BatchDTO> getBatchById(Long id);
    BatchDTO createBatch(BatchDTO batchDTO);
    BatchDTO updateBatch(Long id, BatchDTO batchDTO);
    void deleteBatch(Long id);

    // ✅ NUEVO MÉTODO: Obtener lotes por ID de proveedor
    List<BatchDTO> getBatchesByProviderId(Long providerId);

    // ❌ ELIMINADO: Este método (getBeersByProviderId) no debe estar en BatchService.
    // Si lo necesitas, asegúrate de que esté en BeerService.java
    // List<BeerDTO> getBeersByProviderId(Long providerId);
}