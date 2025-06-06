package beerstar.controller;

import beerstar.dto.BeerDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.model.User; // Importar User
import beerstar.repository.UserRepository; // Importar UserRepository
import beerstar.service.BeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Re-habilitar @PreAuthorize
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/beers")
public class BeerController {

    @Autowired
    private BeerService beerService;

    @Autowired
    private UserRepository userRepository; // Necesario para obtener el ID del usuario autenticado

    @GetMapping
    public ResponseEntity<List<BeerDTO>> getAllBeers() {
        // Este endpoint puede ser público o puede mostrar todas las cervezas si un ADMIN lo pide.
        // Si un PROVIDER accede aquí, debería ser filtrado más adelante por su ID,
        // o se puede añadir un endpoint específico para "mis cervezas".
        // Por ahora, lo dejamos como público si no tiene @PreAuthorize.
        // Si queremos que sea solo para ADMIN, añadimos @PreAuthorize("hasRole('ADMIN')")
        return ResponseEntity.ok(beerService.getAllBeers());
    }

    @GetMapping("/{id}")
    // Un ADMIN puede ver cualquier cerveza, un PROVIDER solo las suyas
    @PreAuthorize("hasRole('CLIENT') or (hasRole('PROVIDER') and @beerServiceImpl.getBeerById(#id).isPresent() and @beerServiceImpl.getBeerById(#id).get().getProviderId() == principal.id)")
    public ResponseEntity<BeerDTO> getBeerById(@PathVariable Long id) {
        // Si el usuario es PROVIDER, la lógica de seguridad a nivel de método ya lo filtrará.
        // Si es ADMIN, simplemente se obtiene.
        return beerService.getBeerById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Cerveza no encontrada con ID: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PROVIDER')") // Solo un PROVIDER puede crear cervezas
    public ResponseEntity<BeerDTO> createBeer(@RequestBody BeerDTO beerDTO) {
        // El servicio BeerServiceImpl ya asignará el provider_id del usuario autenticado
        BeerDTO createdBeer = beerService.createBeer(beerDTO);
        return new ResponseEntity<>(createdBeer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PROVIDER')") // Solo un PROVIDER puede actualizar sus cervezas
    public ResponseEntity<BeerDTO> updateBeer(@PathVariable Long id, @RequestBody BeerDTO beerDTO) {
        // La validación de que la cerveza pertenece al proveedor se hace en el servicio
        BeerDTO updatedBeer = beerService.updateBeer(id, beerDTO);
        return ResponseEntity.ok(updatedBeer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PROVIDER')") // Solo un PROVIDER puede eliminar sus cervezas
    public ResponseEntity<Void> deleteBeer(@PathVariable Long id) {
        // La validación de que la cerveza pertenece al proveedor se hace en el servicio
        beerService.deleteBeer(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ NUEVO ENDPOINT: Para que los proveedores obtengan solo sus cervezas
    @GetMapping("/my-beers")
    @PreAuthorize("hasAuthority('PROVIDER')")
    public ResponseEntity<List<BeerDTO>> getMyBeers() {
        // Obtener el ID del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Esto es el email del usuario
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        List<BeerDTO> myBeers = beerService.getBeersByProviderId(currentUser.getId());
        return ResponseEntity.ok(myBeers);
    }
}