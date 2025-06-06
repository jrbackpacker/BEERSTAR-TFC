// beerstar/config/DataLoader.java
package beerstar.config;

import beerstar.model.Role;
import beerstar.repository.RoleRepository;
import beerstar.service.AuthService; // Importa tu interfaz AuthService
import org.springframework.beans.factory.annotation.Autowired; // Necesario para @Autowired
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Autowired // Inyecta AuthService
    private AuthService authService;

    @Bean
    public CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            // Verifica si el rol "CLIENT" ya existe
            if (roleRepository.findByName("CLIENT").isEmpty()) {
                Role clientRole = new Role();
                clientRole.setName("CLIENT"); // Nombre del rol
                roleRepository.save(clientRole);
                System.out.println("Rol 'CLIENT' creado en la base de datos.");
            }

            if (roleRepository.findByName("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
                System.out.println("Rol ADMIN' creado en la base de datos.");
            }

            if (roleRepository.findByName("PROVIDER").isEmpty()) {
                Role providerRole = new Role();
                providerRole.setName("PROVIDER");
                roleRepository.save(providerRole);
                System.out.println("Rol 'PROVIDER' creado en la base de datos.");
            }

        };
    }
}