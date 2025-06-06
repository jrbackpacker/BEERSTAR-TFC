package beerstar.service;

import beerstar.dto.AuthResponse;
import beerstar.dto.LoginRequest;
import beerstar.dto.RegisterRequest;
import beerstar.exception.ResourceNotFoundException;
import beerstar.exception.UserAlreadyExistsException;
import beerstar.model.Role;
import beerstar.model.User;
import beerstar.model.Provider; // Asegúrate de que Provider se importa correctamente
import beerstar.repository.ProviderRepository;
import beerstar.repository.RoleRepository;
import beerstar.repository.UserRepository;
import beerstar.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // ... (Este método ya debería funcionar) ...
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado después de autenticación exitosa."));

        String jwtToken = jwtUtil.generateToken(userDetails);

        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMensaje("Inicio de sesión exitoso.");
        response.setToken(jwtToken);
        response.setIdUsuario(user.getId());
        response.setEmail(user.getEmail());
        response.setTipoUsuario(user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("UNKNOWN"));
        response.setListaRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        response.setType("Bearer");

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_PROVIDER"))) {
            Optional<Provider> providerOptional = providerRepository.findById(user.getId());
            if (providerOptional.isPresent()) {
                Provider provider = providerOptional.get();
                response.setNombre(provider.getName());
                response.setTelefono(provider.getPhone());
                response.setDireccion(provider.getAddress());
            } else {
                response.setNombre(user.getNombre());
                response.setTelefono(user.getTelefono());
                response.setDireccion(user.getDireccion());
            }
        } else {
            response.setNombre(user.getNombre());
            response.setTelefono(user.getTelefono());
            response.setDireccion(user.getDireccion());
        }
        return response;
    }


    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMensaje("El email ya está en uso.");
            return response;
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setNombre(registerRequest.getNombre());
        newUser.setTelefono(registerRequest.getTelefono());
        newUser.setDireccion(registerRequest.getDireccion ());

        Role role = roleRepository.findByName(registerRequest.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + registerRequest.getRoleName()));
        newUser.setRoles(Collections.singleton(role));

        User savedUser = userRepository.save(newUser); // Primero guarda el User para que tenga un ID

        if (role.getName().equals("ROLE_PROVIDER")) {
            Provider newProvider = new Provider();
            // ¡IMPORTANTE! Con @MapsId, no necesitas setear el ID manualmente.
            // Simplemente asigna la entidad User y Hibernate se encargará del ID.
            newProvider.setUser(savedUser); // Establece la relación OneToOne con el User recién guardado
            newProvider.setName(registerRequest.getNombre());
            newProvider.setAddress(registerRequest.getDireccion());
            newProvider.setPhone(registerRequest.getTelefono());
            newProvider.setFechaRegistro(LocalDateTime.now());
            // newProvider.setLogoUrl(""); // Puedes establecer un valor por defecto
            providerRepository.save(newProvider); // Guarda el proveedor
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(),
                savedUser.getPassword(),
                savedUser.getRoles().stream().map(r -> (GrantedAuthority) () -> r.getName()).collect(Collectors.toList())
        );
        String jwtToken = jwtUtil.generateToken(userDetails);

        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMensaje("Registro exitoso.");
        response.setToken(jwtToken);
        response.setIdUsuario(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setTipoUsuario(role.getName());
        response.setListaRoles(Collections.singletonList(role.getName()));
        response.setType("Bearer");
        response.setNombre(savedUser.getNombre());
        response.setTelefono(savedUser.getTelefono());
        response.setDireccion(savedUser.getDireccion());

        return response;
    }

}