// app/src/main/java/com/beerstar/beerstar/response/responsePedidoItem.java
package com.beerstar.beerstar.response;

import com.google.gson.annotations.SerializedName;

// Clase que representa un item dentro de un pedido
public class responsePedidoItem {

    private Long id; // ID del OrderItem en el backend (se genera al guardar)
    @SerializedName("productId")
    private Long productId;
    @SerializedName("description")
    private String description;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("price")
    private double price;
    @SerializedName("productType")
    private String productType; // "BEER" o "BATCH"

    // Constructor vacío
    public responsePedidoItem() {
    }

    // Constructor para crear desde responseProductos
    public responsePedidoItem(Long productId, String productName, String description, String imageUrl, int quantity, double price, String productType) {
        this.productId = productId;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.productType = productType;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {return quantity;}

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @Override
    public String toString() {
        return "responsePedidoItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", productType='" + productType + '\'' +
                '}';
    }
}