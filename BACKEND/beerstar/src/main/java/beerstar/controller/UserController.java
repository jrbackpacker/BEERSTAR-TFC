package beerstar.controller;

import beerstar.dto.UserDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users") // Ruta base para la gestión de usuarios (requiere autenticación)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    // Ya no necesitas un POST para registrar aquí si AuthController lo maneja
    // Si este POST es para crear usuarios por un admin, mantenlo y securízalo
    @PostMapping
    public ResponseEntity<UserDTO> createUserByAdmin(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ¡NUEVO! Endpoint público para obtener proveedores
    // Esto es si quieres que esta lista sea accesible sin autenticación
    @GetMapping("/public/providers") // La ruta completa será /users/public/providers
    public ResponseEntity<List<UserDTO>> getAllProvidersPublic() {
        return ResponseEntity.ok(userService.getAllProviders());
    }
}