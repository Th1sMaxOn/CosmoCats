package org.example.cosmocats.repository;

import org.example.cosmocats.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("select o from Order o join fetch o.lines where o.id = :id")
    Optional<Order> fetchWithLinesById(Long id);
}
