package org.example.cosmocats.repository;

import org.example.cosmocats.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, NaturalIdRepository<OrderEntity, String> {
    @Query("select o from OrderEntity o join fetch o.lines where o.id = :id")
    Optional<OrderEntity> fetchWithLinesById(@Param("id") Long id);

    List<OrderEntity> findAllByCustomer_Email(String email);
}