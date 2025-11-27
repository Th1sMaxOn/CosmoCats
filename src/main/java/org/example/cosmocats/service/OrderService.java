package org.example.cosmocats.service;

import org.example.cosmocats.domain.*;
import org.example.cosmocats.repository.CustomerRepository;
import org.example.cosmocats.repository.OrderRepository;
import org.example.cosmocats.repository.ProductRepository;
import org.example.cosmocats.web.dto.CreateOrderRequest;
import org.example.cosmocats.web.dto.OrderLineDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository
                .findByEmail(request.customerEmail())
                .orElseGet(() -> customerRepository.save(
                        Customer.builder()
                                .email(request.customerEmail())
                                .fullName(request.customerFullName())
                                .build()
                ));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(request.status() == null ? "NEW" : request.status());
        order.setCreatedAt(Instant.now());
        order.setOrderNumber(generateOrderNumber());

        for (OrderLineDto lineDto : request.lines()) {
            Product product = productRepository.findById(lineDto.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + lineDto.productId()));
            OrderLine line = new OrderLine();
            line.setOrder(order);
            line.setProduct(product);
            line.setQuantity(lineDto.quantity());
            BigDecimal price = lineDto.priceAtPurchase() != null
                    ? lineDto.priceAtPurchase()
                    : product.getPrice();
            line.setPriceAtPurchase(price);
            order.getLines().add(line);
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID();
    }
}
