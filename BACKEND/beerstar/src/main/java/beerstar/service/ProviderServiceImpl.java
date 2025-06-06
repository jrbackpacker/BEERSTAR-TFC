package beerstar.service;

import beerstar.dto.ProviderDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.model.Provider;
import beerstar.model.User; // Importamos User
import beerstar.repository.ProviderRepository;
import beerstar.repository.UserRepository; // Necesitamos el UserRepository para el email del User
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private UserRepository userRepository; // Para obtener el email del User asociado al Provider

    @Override
    public List<ProviderDTO> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProviderDTO> getProviderById(Long id) {
        return providerRepository.findById(id)
                .map(this::mapToDTO);
    }

    // Método de mapeo de entidad a DTO
    private ProviderDTO mapToDTO(Provider provider) {
        ProviderDTO dto = new ProviderDTO();
        dto.setId(provider.getId()); // CAMBIO: Usar getId()
        dto.setName(provider.getName());
        dto.setAddress(provider.getAddress());
        dto.setPhone(provider.getPhone());
        dto.setRegistration_date(provider.getFechaRegistro());
        dto.setLogoUrl(provider.getLogoUrl());

        // Obtener el email del User asociado
        if (provider.getUser() != null) {
            dto.setEmail(provider.getUser().getEmail());
        } else {
            userRepository.findById(provider.getId()).ifPresent(user -> dto.setEmail(user.getEmail())); // CAMBIO: Usar getId()
        }
        return dto;
    }
}