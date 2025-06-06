package beerstar.controller;

import beerstar.dto.BatchDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.model.User;
import beerstar.repository.UserRepository;
import beerstar.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Re-habilitar @PreAuthorize
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/batches")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @Autowired
    private UserRepository userRepository; // Necesario para obtener el ID del usuario autenticado

    @GetMapping
    public ResponseEntity<List<BatchDTO>> getAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @GetMapping("/{id}")
    // Un ADMIN puede ver cualquier lote, un PROVIDER solo los suyos
    // Nota: el @batchServiceImpl se refiere a un bean de Spring. Asegúrate de que el nombre del bean sea correcto (por defecto, es el nombre de la clase en camelCase, es decir, batchServiceImpl)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVIDER') and @batchServiceImpl.getBatchById(#id).isPresent() and @batchServiceImpl.getBatchById(#id).get().getProviderId() == principal.id)")
    public ResponseEntity<BatchDTO> getBatchById(@PathVariable Long id) {
        return batchService.getBatchById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Lote de cerveza no encontrado con ID: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PROVIDER')") // Solo un PROVIDER puede crear lotes
    public ResponseEntity<BatchDTO> createBatch(@RequestBody BatchDTO batchDTO) {
        BatchDTO createdBatch = batchService.createBatch(batchDTO);
        return new ResponseEntity<>(createdBatch, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PROVIDER')") // Solo un PROVIDER puede actualizar sus lotes
    public ResponseEntity<BatchDTO> updateBatch(@PathVariable Long id, @RequestBody BatchDTO batchDTO) {
        // La validación de que el lote pertenece al proveedor se hace en el servicio
        BatchDTO updatedBatch = batchService.updateBatch(id, batchDTO);
        return ResponseEntity.ok(updatedBatch);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PROVIDER')") // Solo un PROVIDER puede eliminar sus lotes
    public ResponseEntity<Void> deleteBatch(@PathVariable Long id) {
        // La validación de que el lote pertenece al proveedor se hace en el servicio
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ NUEVO ENDPOINT: Para que los proveedores obtengan solo sus lotes
    @GetMapping("/my-batches")
    @PreAuthorize("hasAuthority('PROVIDER')")
    public ResponseEntity<List<BatchDTO>> getMyBatches() {
        // Obtener el ID del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Esto es el email del usuario
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        List<BatchDTO> myBatches = batchService.getBatchesByProviderId(currentUser.getId());
        return ResponseEntity.ok(myBatches);
    }
}