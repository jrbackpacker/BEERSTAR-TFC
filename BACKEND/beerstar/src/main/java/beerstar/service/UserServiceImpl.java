package beerstar.service;

import beerstar.dto.UserDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.exception.UserAlreadyExistsException;
import beerstar.model.User;
import beerstar.model.Role;
import beerstar.repository.UserRepository;
import beerstar.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty() ||
                userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Email and password are required for user registration.");
        }

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("El email " + userDTO.getEmail() + " ya está registrado.");
        }

        User newUser = new User();
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setNombre(userDTO.getNombre());
        newUser.setTelefono(userDTO.getTelefono());
        newUser.setDireccion(userDTO.getDireccion());

        // Asigna el rol por defecto (por ejemplo, "ROLE_CLIENT") si no se especifica
        Role defaultRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'ROLE_CLIENT' no encontrado."));
        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        newUser.setRoles(roles);

        User savedUser = userRepository.save(newUser);
        return convertToDTO(savedUser);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty() && !userDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("El email " + userDTO.getEmail() + " ya está en uso.");
            }
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getNombre() != null) {
            existingUser.setNombre(userDTO.getNombre());
        }
        if (userDTO.getTelefono() != null) {
            existingUser.setTelefono(userDTO.getTelefono());
        }
        if (userDTO.getDireccion() != null) {
            existingUser.setDireccion(userDTO.getDireccion());
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> getAllProviders() {
        // Asumiendo que el rol para proveedores es "ROLE_PROVIDER"
        // Asegúrate de que UserRepository tiene un método findByRoles_Name(String name)
        return userRepository.findByRoles_Name("ROLE_PROVIDER").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        // NO DEVOLVER LA CONTRASEÑA EN EL DTO POR SEGURIDAD
        dto.setNombre(user.getNombre());
        dto.setTelefono(user.getTelefono());
        dto.setDireccion(user.getDireccion());
        // Mapea los roles a una lista de nombres de rol (String)
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        return dto;
    }
}