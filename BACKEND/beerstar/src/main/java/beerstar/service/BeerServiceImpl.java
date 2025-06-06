package beerstar.service;

import beerstar.dto.BeerDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.model.Beer;
import beerstar.model.User; // Necesario para el provider
import beerstar.repository.BeerRepository;
import beerstar.repository.UserRepository; // Necesario para buscar al provider
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BeerServiceImpl implements BeerService { // Implementa la interfaz BeerService

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private UserRepository userRepository; // Para manejar el provider

    @Override
    public List<BeerDTO> getAllBeers() {
        return beerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BeerDTO> getBeerById(Long id) {
        return beerRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public BeerDTO createBeer(BeerDTO beerDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        Beer beer = mapToEntity(beerDTO);
        beer.setProvider(authenticatedUser); // Asignar el proveedor autenticado

        Beer createdBeer = beerRepository.save(beer);
        return mapToDTO(createdBeer);
    }

    @Override
    public BeerDTO updateBeer(Long id, BeerDTO beerDTO) {
        Beer existingBeer = beerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cerveza no encontrada con ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        if (!existingBeer.getProvider().getId().equals(authenticatedUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("No tiene permiso para actualizar esta cerveza.");
        }

        // Actualizar campos del existente con los del DTO
        existingBeer.setNombre(beerDTO.getNombre());
        existingBeer.setDescripcion(beerDTO.getDescripcion());
        existingBeer.setPrecio(beerDTO.getPrecio());
        existingBeer.setImagen(beerDTO.getImagen());
        existingBeer.setStock(beerDTO.getStock());
        existingBeer.setGraduacion(beerDTO.getGraduacion());
        existingBeer.setIdCategoria(beerDTO.getIdCategoria());
        existingBeer.setNombreCategoria(beerDTO.getNombreCategoria());

        // El proveedor no se cambia en una actualización
        // if (beerDTO.getProviderId() != null) { // Esta lógica ya no es necesaria aquí, el proveedor es el autenticado
        //     User provider = userRepository.findById(beerDTO.getProviderId())
        //             .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + beerDTO.getProviderId()));
        //     existingBeer.setProvider(provider);
        // } else {
        //     existingBeer.setProvider(null);
        // }

        Beer updatedBeer = beerRepository.save(existingBeer);
        return mapToDTO(updatedBeer);
    }

    @Override
    public void deleteBeer(Long id) {
        Beer existingBeer = beerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cerveza no encontrada con ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        if (!existingBeer.getProvider().getId().equals(authenticatedUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("No tiene permiso para eliminar esta cerveza.");
        }

        beerRepository.deleteById(id);
    }

    @Override
    public List<BeerDTO> getBeersByProviderId(Long providerId) {
        return beerRepository.findByProvider_Id(providerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar privado para mapear Entidad a DTO (para obtener datos)
    private BeerDTO mapToDTO(Beer beer) {
        BeerDTO beerDTO = new BeerDTO();
        beerDTO.setId(beer.getId());
        beerDTO.setNombre(beer.getNombre());
        beerDTO.setDescripcion(beer.getDescripcion());
        beerDTO.setPrecio(beer.getPrecio());
        beerDTO.setImagen(beer.getImagen());
        beerDTO.setStock(beer.getStock());

        if (beer.getGraduacion() != null) {
            beerDTO.setGraduacion(beer.getGraduacion());
        } else {
            beerDTO.setGraduacion(0.0);
        }

        beerDTO.setIdCategoria(beer.getIdCategoria());
        beerDTO.setNombreCategoria(beer.getNombreCategoria());

        if (beer.getProvider() != null) {
            beerDTO.setProviderId(beer.getProvider().getId());
            // beerDTO.setProviderName(beer.getProvider().getUsername()); // ¡Línea eliminada!
            // Si necesitas el nombre del proveedor en el DTO, dime qué método usar de la entidad User (ej. getNombre(), getName())
        }
        return beerDTO;
    }

    // Método auxiliar privado para mapear DTO a Entidad (para crear/actualizar)
    private Beer mapToEntity(BeerDTO beerDTO) {
        Beer beer = new Beer();
        if (beerDTO.getId() != null) {
            beer.setId(beerDTO.getId());
        }
        beer.setNombre(beerDTO.getNombre());
        beer.setDescripcion(beerDTO.getDescripcion());
        beer.setPrecio(beerDTO.getPrecio());
        beer.setImagen(beerDTO.getImagen());
        beer.setStock(beerDTO.getStock());
        beer.setGraduacion(beerDTO.getGraduacion());
        beer.setIdCategoria(beerDTO.getIdCategoria());
        beer.setNombreCategoria(beerDTO.getNombreCategoria());
        return beer;
    }
}