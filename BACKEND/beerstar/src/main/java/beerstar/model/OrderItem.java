// beerstar/model/OrderItem.java
package beerstar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beer_id")
    private Beer beer; // Relación con Beer (si el producto es una cerveza)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch; // Relación con Batch (si el producto es un lote)

    // Información del producto (duplicada para persistencia histórica y sin relación con el objeto original)
    @Column(name = "product_id", nullable = false)
    private Long productId; // ID del producto (Beer o Batch)

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price; // Precio unitario del producto al momento de la compra

    @Column(name = "product_type", nullable = false, length = 50) // "BEER" o "BATCH"
    private String productType;

    // Método de conveniencia para obtener el ID del proveedor del producto asociado
    // Ahora que Beer y Batch tienen getProviderId(), esto funcionará.
    public Long getProviderId() {
        if ("BEER".equalsIgnoreCase(this.productType) && this.beer != null) {
            return this.beer.getProviderId();
        } else if ("BATCH".equalsIgnoreCase(this.productType) && this.batch != null) {
            return this.batch.getProviderId();
        }
        return null; // Retorna null si no hay un producto asociado o el tipo es desconocido
    }
}