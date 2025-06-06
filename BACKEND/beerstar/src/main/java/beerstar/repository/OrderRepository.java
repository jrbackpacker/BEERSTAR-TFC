package beerstar.repository;

import beerstar.model.Order; // Asegúrate de que esta importación sea correcta para tu nueva entidad Order
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Método existente para buscar pedidos por el ID del usuario cliente
    List<Order> findByUserId(Long userId);

    // NUEVO MÉTODO: Para que un proveedor pueda ver todos los pedidos que le conciernen
    List<Order> findByProviderId(Long providerId);

    // Puedes añadir métodos adicionales si necesitas buscar por estado, fecha, etc.
    // List<Order> findByProviderIdAndStatus(Long providerId, Order.OrderStatus status);
    // List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);
}