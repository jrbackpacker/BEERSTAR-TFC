package beerstar.controller;

import beerstar.dto.OrderDTO;
import beerstar.exception.ResourceNotFoundException;
import beerstar.model.User;
import beerstar.repository.UserRepository;
import beerstar.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER', 'ADMIN')")

    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or " +
            "(hasAuthority('PROVIDER') and @orderServiceImpl.getOrderById(#id).isPresent() and @orderServiceImpl.getOrderById(#id).get().getProviderId() == principal.id) or " +
            "(hasAuthority('CLIENT') and @orderServiceImpl.getOrderById(#id).isPresent() and @orderServiceImpl.getOrderById(#id).get().getUserId() == principal.id)")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
    }

    @PostMapping

    @PreAuthorize("hasAnyAuthority('CLIENT', 'PROVIDER, ADMIN')")
    public ResponseEntity<List<OrderDTO>> createOrder(@RequestBody OrderDTO orderDTO) {
        List<OrderDTO> createdOrders = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROVIDER')")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {


        OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PROVIDER')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")


    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('CLIENT') and #userId == principal.id)")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }


    @GetMapping("/my-orders-as-provider")
    @PreAuthorize("hasAuthority('PROVIDER')")
    public ResponseEntity<List<OrderDTO>> getMyOrdersAsProvider() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado."));

        List<OrderDTO> providerOrders = orderService.getOrdersByProviderId(currentUser.getId());
        return ResponseEntity.ok(providerOrders);
    }
}