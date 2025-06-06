// beerstar/dto/OrderItemDTO.java
package beerstar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String description;
    private String imageUrl;
    private int quantity;
    private double price;
    private String productType; // "BEER" o "BATCH"

    // Nuevo: Información del proveedor para este OrderItem
    private Long providerId;
    private String providerName; // Nombre del proveedor del producto en este OrderItem
}