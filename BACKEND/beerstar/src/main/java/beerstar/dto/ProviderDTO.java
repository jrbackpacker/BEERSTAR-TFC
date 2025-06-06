package beerstar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProviderDTO {
    private Long id; // CAMBIO: Usar 'id' para consistencia con la entidad Provider
    private String name;
    private String address;
    private String phone;
    private String email;
    private LocalDateTime registration_date;
    private String logoUrl;
}