package beerstar.service;

import beerstar.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderDTO> getAllOrders();
    Optional<OrderDTO> getOrderById(Long id);
    // Cambiamos el tipo de retorno a List<OrderDTO> porque un único pedido de cliente
    // puede generar múltiples pedidos internos (uno por proveedor).
    List<OrderDTO> createOrder(OrderDTO orderDTO); // Modificado para devolver una lista
    OrderDTO updateOrder(Long id, OrderDTO orderDTO);
    void deleteOrder(Long id);
    List<OrderDTO> getOrdersByUserId(Long userId);

    // NUEVO MÉTODO: Para que los proveedores puedan ver sus pedidos
    List<OrderDTO> getOrdersByProviderId(Long providerId);
}