package beerstar.dto;

import beerstar.model.Order; // Importa la clase enum Order.OrderStatus
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId; // ID del cliente
    private String userEmail; // Email del cliente
    private String userName; // Nombre del cliente (si lo quieres mostrar)

    // Método setProviderId completado
    // Agregamos el ID del proveedor al DTO, ya que es necesario para la lógica de negocio
    private Long providerId;
    private String providerName; // Nombre del proveedor (opcional, si se quiere mostrar en el frontend)

    private LocalDateTime orderDate;
    private String status; // Usamos String para el estado del enum OrderStatus
    private double totalAmount; // El total debe ser el de los items originales, o el total de los items filtrados?
    // Para un proveedor, el total podría ser solo de sus items.
    // Si no, el total del pedido original es más lógico.
    private String shippingAddress;
    private String paymentMethod;

    // Lista de OrderItems, que para el proveedor, estarán filtrados
    private List<OrderItemDTO> items; // La lista de OrderItemDTOs

}