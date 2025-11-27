package org.example.cosmocats.web;

import org.example.cosmocats.domain.Order;
import org.example.cosmocats.service.OrderService;
import org.example.cosmocats.web.dto.CreateOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + order.getId()))
                .body(order);
    }

    @GetMapping("/{orderNumber}")
    public Order getByOrderNumber(@PathVariable String orderNumber) {
        return orderService.getOrderByOrderNumber(orderNumber);
    }
}
