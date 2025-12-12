package org.example.cosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cosmocats.domain.Order;
import org.example.cosmocats.mapper.OrderMapper;
import org.example.cosmocats.service.OrderService;
import org.example.cosmocats.web.dto.CreateOrderRequest;
import org.example.cosmocats.web.dto.OrderResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    // 1. Створити замовлення
    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@RequestBody @Valid CreateOrderRequest request) {
        Order domain = orderMapper.toDomain(request);
        Order saved = orderService.createOrder(domain);

        OrderResponseDto response = orderMapper.toDto(saved);

        return ResponseEntity.created(URI.create("/api/orders/" + saved.getId()))
                .body(response);
    }

    // 2. Отримати всі АБО знайти по Email (GET /api/orders?email=user@example.com)
    @GetMapping
    public List<OrderResponseDto> getAll(@RequestParam(required = false) String email) {
        List<Order> orders;

        if (email != null && !email.isBlank()) {
            // Якщо передали email - шукаємо по ньому
            orders = orderService.findByCustomerEmail(email);
        } else {
            // Якщо ні - віддаємо всі
            orders = orderService.findAll();
        }

        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    // 3. Отримати по номеру замовлення (Natural ID)
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getByOrderNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNaturalId(orderNumber);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    // 4. Отримати по внутрішньому ID
    @GetMapping("/id/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    // 5. Оновити статус (PATCH /api/orders/{orderNumber}/status?newStatus=SHIPPED)
    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable String orderNumber,
            @RequestParam String newStatus) {

        Order updated = orderService.updateStatus(orderNumber, newStatus);
        return ResponseEntity.ok(orderMapper.toDto(updated));
    }

    // 6. Видалити замовлення
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}