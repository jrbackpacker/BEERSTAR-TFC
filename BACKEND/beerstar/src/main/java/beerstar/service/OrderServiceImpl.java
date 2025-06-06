package beerstar.service;

import beerstar.dto.OrderDTO;
import beerstar.dto.OrderItemDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.model.*;
import beerstar.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id).map(this::convertToDto);
    }

    @Override
    @Transactional
    public List<OrderDTO> createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + orderDTO.getUserId()));

        Map<Long, List<OrderItemDTO>> itemsByProvider = new HashMap<>();

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Long providerId;
            if ("BEER".equalsIgnoreCase(itemDTO.getProductType())) {
                Beer beer = beerRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cerveza no encontrada con ID: " + itemDTO.getProductId()));

                // **** CAMBIO AQUÍ ****
                if (beer.getProvider() == null || beer.getProvider().getId() == null) {
                    throw new ResourceNotFoundException("La cerveza con ID " + itemDTO.getProductId() + " no tiene un proveedor asociado.");
                }
                providerId = beer.getProvider().getId();
                // **** FIN DEL CAMBIO ****

                itemDTO.setProductName(beer.getNombre());
                itemDTO.setDescription(beer.getDescripcion());
                itemDTO.setImageUrl(beer.getImagen());
            } else if ("BATCH".equalsIgnoreCase(itemDTO.getProductType())) {
                Batch batch = batchRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado con ID: " + itemDTO.getProductId()));

                // **** CAMBIO AQUÍ ****
                if (batch.getProvider() == null || batch.getProvider().getId() == null) {
                    throw new ResourceNotFoundException("El lote con ID " + itemDTO.getProductId() + " no tiene un proveedor asociado.");
                }
                providerId = batch.getProvider().getId();
                // **** FIN DEL CAMBIO ****

                itemDTO.setProductName(batch.getNombre());
                itemDTO.setDescription(batch.getDescripcion());
                itemDTO.setImageUrl(batch.getImagenUrl());
            } else {
                throw new IllegalArgumentException("Tipo de producto desconocido: " + itemDTO.getProductType());
            }
            itemsByProvider.computeIfAbsent(providerId, k -> new ArrayList<>()).add(itemDTO);
        }

        List<OrderDTO> createdOrders = new ArrayList<>();

        for (Map.Entry<Long, List<OrderItemDTO>> entry : itemsByProvider.entrySet()) {
            Long providerUserId = entry.getKey(); // Este es el ID del usuario que actúa como proveedor
            List<OrderItemDTO> providerItems = entry.getValue();

            // Buscar la entidad Provider utilizando el ID del usuario proveedor
            Provider provider = providerRepository.findById(providerUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado en la tabla de proveedores para el User ID: " + providerUserId));

            Order order = new Order();
            order.setUser(user);
            order.setProvider(provider.getUser());
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(Order.OrderStatus.PENDING);
            order.setShippingAddress(orderDTO.getShippingAddress());
            order.setPaymentMethod(orderDTO.getPaymentMethod());

            double totalAmount = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (OrderItemDTO itemDTO : providerItems) {
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(itemDTO, orderItem);

                if ("BEER".equalsIgnoreCase(itemDTO.getProductType())) {
                    Beer beer = beerRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Cerveza no encontrada con ID: " + itemDTO.getProductId()));
                    orderItem.setBeer(beer);
                    orderItem.setBatch(null);
                } else if ("BATCH".equalsIgnoreCase(itemDTO.getProductType())) {
                    Batch batch = batchRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado con ID: " + itemDTO.getProductId()));
                    orderItem.setBatch(batch);
                    orderItem.setBeer(null);
                }

                orderItem.setOrder(order);
                orderItems.add(orderItem);
                totalAmount += itemDTO.getQuantity() * itemDTO.getPrice();
            }
            order.setItems(orderItems);
            order.setTotalAmount(totalAmount);

            Order savedOrder = orderRepository.save(order);
            createdOrders.add(convertToDto(savedOrder));
        }

        return createdOrders;
    }


    @Override
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        // Actualiza los campos principales del pedido
        // Corrección del error: Convertir String a Order.OrderStatus enum
        try {
            existingOrder.setStatus(Order.OrderStatus.valueOf(orderDTO.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pedido inválido: " + orderDTO.getStatus(), e);
        }

        existingOrder.setShippingAddress(orderDTO.getShippingAddress());
        existingOrder.setPaymentMethod(orderDTO.getPaymentMethod());
        // No se suele permitir actualizar totalAmount o userId directamente desde aquí

        // Lógica para actualizar los ítems del pedido (ej. eliminar viejos, añadir nuevos, actualizar existentes)
        // Esto puede ser complejo y depende de tus reglas de negocio.
        // Por simplicidad, aquí solo actualizamos los ítems existentes en el DTO
        // Podrías necesitar un manejo más robusto para add/remove items
        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            // Un enfoque simple: eliminar todos los ítems actuales y añadir los nuevos
            existingOrder.getItems().clear(); // Limpia los ítems existentes
            double newTotalAmount = 0.0;
            for (OrderItemDTO itemDTO : orderDTO.getItems()) {
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(itemDTO, orderItem);

                // Asegúrate de reestablecer la referencia Beer o Batch
                if ("BEER".equalsIgnoreCase(itemDTO.getProductType())) {
                    Beer beer = beerRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Cerveza no encontrada con ID: " + itemDTO.getProductId()));
                    orderItem.setBeer(beer);
                    orderItem.setBatch(null);
                } else if ("BATCH".equalsIgnoreCase(itemDTO.getProductType())) {
                    Batch batch = batchRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado con ID: " + itemDTO.getProductId()));
                    orderItem.setBatch(batch);
                    orderItem.setBeer(null);
                }

                orderItem.setOrder(existingOrder); // Vincular al pedido existente
                existingOrder.getItems().add(orderItem);
                newTotalAmount += itemDTO.getQuantity() * itemDTO.getPrice();
            }
            existingOrder.setTotalAmount(newTotalAmount);
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        return convertToDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByProviderId(Long providerId) {
        return orderRepository.findByProviderId(providerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    // --- Métodos de Conversión de Entidad a DTO ---

    private OrderDTO convertToDto(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO); // Copia id, orderDate, status, totalAmount

        // Mapear el ID del usuario cliente
        if (order.getUser() != null) {
            orderDTO.setUserId(order.getUser().getId());
            orderDTO.setUserEmail(order.getUser().getEmail()); // Agregado
            orderDTO.setUserName(order.getUser().getNombre()); // Agregado
        }

        // Mapear el ID y nombre del proveedor
        if (order.getProvider() != null) {
            orderDTO.setProviderId(order.getProvider().getId());
            orderDTO.setProviderName(order.getProvider().getNombre()); // Agregado
        }

        // Mapear la dirección de envío y el método de pago
        orderDTO.setShippingAddress(order.getShippingAddress());
        orderDTO.setPaymentMethod(order.getPaymentMethod());

        // Mapear los ítems del pedido
        orderDTO.setItems(order.getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
        return orderDTO;
    }

    private OrderItemDTO convertToDto(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        BeanUtils.copyProperties(orderItem, orderItemDTO);

        // Asegurarse de que el providerId y providerName se mapeen para OrderItemDTO
        if (orderItem.getBeer() != null && orderItem.getBeer().getProvider() != null) {
            orderItemDTO.setProviderId(orderItem.getBeer().getProvider().getId());
            orderItemDTO.setProviderName(orderItem.getBeer().getProvider().getNombre());
        } else if (orderItem.getBatch() != null && orderItem.getBatch().getProvider() != null) {
            orderItemDTO.setProviderId(orderItem.getBatch().getProvider().getId());
            orderItemDTO.setProviderName(orderItem.getBatch().getProvider().getNombre());
        }

        return orderItemDTO;
    }
}