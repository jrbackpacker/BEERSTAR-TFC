package beerstar.service;

import beerstar.dto.BatchDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.model.Batch;
import beerstar.model.User;
import beerstar.repository.BatchRepository;
import beerstar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements BatchService {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<BatchDTO> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BatchDTO> getBatchById(Long id) {
        return batchRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public BatchDTO createBatch(BatchDTO batchDTO) {
        // Obtener el usuario autenticado (proveedor)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName(); // El email del usuario
        User authenticatedUser = userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        Batch batch = convertToEntity(batchDTO);
        // Asignar el proveedor autenticado al lote
        batch.setProvider(authenticatedUser);

        Batch createdBatch = batchRepository.save(batch);
        return convertToDto(createdBatch);
    }

    @Override
    public BatchDTO updateBatch(Long id, BatchDTO batchDTO) {
        Batch existingBatch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote de cerveza no encontrado con ID: " + id));

        // Obtener el usuario autenticado (proveedor)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName(); // El email del usuario
        User authenticatedUser = userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        // **IMPORTANTE**: Verificar que el lote pertenece al proveedor autenticado
        if (!existingBatch.getProvider().getId().equals(authenticatedUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("No tiene permiso para actualizar este lote.");
        }

        // Actualizar campos del existente con los del DTO
        existingBatch.setNombre(batchDTO.getNombre());
        existingBatch.setPrecio(batchDTO.getPrecio());
        existingBatch.setStock(batchDTO.getStock());
        existingBatch.setDescripcion(batchDTO.getDescripcion());
        existingBatch.setImagenUrl(batchDTO.getUrl());
        // El proveedor no se cambia en una actualización

        Batch updatedBatch = batchRepository.save(existingBatch);
        return convertToDto(updatedBatch);
    }

    @Override
    public void deleteBatch(Long id) {
        Batch existingBatch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote de cerveza no encontrado con ID: " + id));

        // Obtener el usuario autenticado (proveedor)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName(); // El email del usuario
        User authenticatedUser = userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        // **IMPORTANT**: Verificar que el lote pertenece al proveedor autenticado
        if (!existingBatch.getProvider().getId().equals(authenticatedUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("No tiene permiso para eliminar este lote.");
        }

        batchRepository.deleteById(id);
    }

    @Override
    public List<BatchDTO> getBatchesByProviderId(Long providerId) {
        // Usa findByProvider_Id para buscar lotes por el ID del proveedor
        return batchRepository.findByProvider_Id(providerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BatchDTO convertToDto(Batch batch) {
        BatchDTO batchDTO = new BatchDTO();
        batchDTO.setId(batch.getId());
        batchDTO.setNombre(batch.getNombre());
        batchDTO.setPrecio(batch.getPrecio());
        batchDTO.setStock(batch.getStock());
        batchDTO.setDescripcion(batch.getDescripcion());
        batchDTO.setUrl(batch.getImagenUrl());

        if (batch.getProvider() != null) {
            batchDTO.setProviderId(batch.getProvider().getId());
            // Si también quieres el nombre del proveedor en el DTO del lote
            // batchDTO.setProviderName(batch.getProvider().getNombre()); // Asumiendo que User tiene getNombre()
        }
        return batchDTO;
    }

    private Batch convertToEntity(BatchDTO batchDTO) {
        Batch batch = new Batch();

        batch.setNombre(batchDTO.getNombre());
        batch.setPrecio(batchDTO.getPrecio());
        batch.setStock(batchDTO.getStock());
        batch.setDescripcion(batchDTO.getDescripcion());
        batch.setImagenUrl(batchDTO.getUrl());

        return batch;
    }
}