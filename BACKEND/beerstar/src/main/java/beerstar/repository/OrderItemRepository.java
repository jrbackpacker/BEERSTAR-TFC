// beerstar/repository/OrderItemRepository.java
package beerstar.repository;

import beerstar.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Este método no es directamente findByProviderId, sino que se usará en el servicio
    // para construir la lógica compleja.

    // Ejemplo: Puedes necesitar buscar OrderItems por Order y luego filtrar en memoria,
    // o hacer una consulta JPQL compleja si la base de datos es muy grande.
    // Para empezar, haremos el filtrado en el servicio.

    // Puedes necesitar un método para buscar OrderItems de un pedido específico
    List<OrderItem> findByOrderId(Long orderId);

    // O para buscar OrderItems que contengan una cerveza de un proveedor específico
    // Esto implicaría un JOIN en la base de datos.
    // @Query("SELECT oi FROM OrderItem oi JOIN oi.beer b WHERE b.provider.id = :providerId")
    // List<OrderItem> findByBeerProviderId(@Param("providerId") Long providerId);

    // @Query("SELECT oi FROM OrderItem oi JOIN oi.batch bt WHERE bt.provider.id = :providerId")
    // List<OrderItem> findByBatchProviderId(@Param("providerId") Long providerId);

    // Una consulta para obtener OrderItems que pertenecen a un proveedor, ya sea por cerveza o por lote.
    // Esto es más complejo, pero si el rendimiento lo requiere, es la forma.
    // Para simplificar, la lógica de unir los resultados de Beer y Batch o filtrar en el servicio es más sencilla para empezar.
    // Sin embargo, si quieres que la DB haga la mayor parte, esta sería la consulta:
    @Query("SELECT oi FROM OrderItem oi LEFT JOIN oi.beer b LEFT JOIN oi.batch bt WHERE " +
           "(oi.productType = 'BEER' AND b.provider.id = :providerId) OR " +
           "(oi.productType = 'BATCH' AND bt.provider.id = :providerId)")
    List<OrderItem> findByProductProviderId(@Param("providerId") Long providerId);

    // Y para obtener los pedidos completos que tienen *algún* item de un proveedor
    // Necesitaríamos obtener los OrderItems, y luego los Orders a los que pertenecen.
    // Esto se gestionará mejor en el OrderService.
}