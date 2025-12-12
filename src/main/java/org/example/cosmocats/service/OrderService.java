package org.example.cosmocats.service;

import org.example.cosmocats.domain.Order;
import java.util.List;

public interface OrderService {

    Order createOrder(Order orderDomain);

    Order getOrderByNaturalId(String orderNumber);

    Order getById(Long id);

    List<Order> findAll();

    List<Order> findByCustomerEmail(String email);

    Order updateStatus(String orderNumber, String newStatus);

    void deleteOrder(Long id);
}