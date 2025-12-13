package org.example.cosmocats.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cosmocats.domain.Order;
import org.example.cosmocats.mapper.OrderMapper;
import org.example.cosmocats.repository.OrderRepository;
import org.example.cosmocats.repository.ProductRepository;
import org.example.cosmocats.repository.entity.CustomerEntity;
import org.example.cosmocats.repository.entity.OrderEntity;
import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.repository.exception.PersistenceException;
import org.example.cosmocats.service.CustomerService;
import org.example.cosmocats.service.OrderService;
import org.example.cosmocats.service.exception.OrderNotFoundException;
import org.example.cosmocats.service.exception.ProductNotFoundException;
import org.example.cosmocats.service.exception.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerService customerService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public Order createOrder(Order orderDomain) {
        try {
            CustomerEntity customerEntity = customerService.findOrCreate(
                    orderDomain.getCustomer().getEmail(),
                    orderDomain.getCustomer().getFullName()
            );

            OrderEntity entity = orderMapper.toEntity(orderDomain);
            entity.setCustomer(customerEntity);

            if (entity.getLines() != null) {
                entity.getLines().forEach(line -> {
                    Long prodId = line.getProduct().getId();
                    ProductEntity realProduct = productRepository.findById(prodId)
                            .orElseThrow(() -> new ProductNotFoundException(prodId));
                    line.setProduct(realProduct);
                    line.setPriceAtPurchase(realProduct.getPrice());
                    line.setOrder(entity);
                });
            }

            OrderEntity savedEntity = orderRepository.save(entity);
            return orderMapper.toDomain(savedEntity);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to create Order", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Order getById(Long id) {
        try {
            return orderRepository.fetchWithLinesById(id)
                    .map(orderMapper::toDomain)
                    .orElseThrow(() -> new OrderNotFoundException(String.valueOf(id)));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to fetch Order by ID", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByNaturalId(String orderNumber) {
        try {
            return orderRepository.findByNaturalId(orderNumber)
                    .map(orderMapper::toDomain)
                    .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to fetch Order by Natural ID", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'BOT')")
    public List<Order> findAll() {
        try {
            return orderRepository.findAll().stream()
                    .map(orderMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PersistenceException("Failed to fetch orders", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCustomerEmail(String email) {
        try {
            return orderRepository.findAllByCustomer_Email(email).stream()
                    .map(orderMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PersistenceException("Failed to find orders for customer: " + email, e);
        }
    }

    @Override
    @Transactional
    public Order updateStatus(String orderNumber, String newStatus) {
        try {
            OrderEntity entity = orderRepository.findByNaturalId(orderNumber)
                    .orElseThrow(() -> new OrderNotFoundException(orderNumber));

            entity.setStatus(newStatus);
            return orderMapper.toDomain(orderRepository.save(entity));

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to update status for order: " + orderNumber, e);
        }
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            throw new PersistenceException("Failed to delete order with id: " + id, e);
        }
    }
}