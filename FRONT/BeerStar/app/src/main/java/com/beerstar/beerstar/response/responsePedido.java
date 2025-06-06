package com.beerstar.beerstar.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Clase que representa un pedido, mapeando OrderDTO del backend
public class responsePedido {

    private Long id;
    @SerializedName("userId") // Coincide con userId del OrderDTO de Spring
    private Long userId;
    @SerializedName("orderDate")
    private String orderDate; // O LocalDateTime si usas una librería de fecha/hora en Android
    @SerializedName("status")
    private String status;    // OrderStatus en String
    @SerializedName("totalAmount")
    private double totalAmount;
    @SerializedName("shippingAddress") // Nuevo campo
    private String shippingAddress;
    @SerializedName("paymentMethod")   // Nuevo campo
    private String paymentMethod;
    @SerializedName("providerId")      // Nuevo campo (lo recibirá en la respuesta)
    private Long providerId;

    @SerializedName("items")
    private List<responsePedidoItem> items; // Lista de ítems del pedido

    // Constructor vacío requerido por Retrofit/Gson
    public responsePedido() {
    }

    // Constructor para crear el pedido desde el cliente (solo los campos que se envían)
    public responsePedido(Long userId, String shippingAddress, String paymentMethod, List<responsePedidoItem> items) {
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }


    // Getters y Setters para todos los campos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public List<responsePedidoItem> getItems() {
        return items;
    }

    public void setItems(List<responsePedidoItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "responsePedido{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderDate='" + orderDate + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", providerId=" + providerId +
                ", items=" + items +
                '}';
    }
}