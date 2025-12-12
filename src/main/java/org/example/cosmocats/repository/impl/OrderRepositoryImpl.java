package org.example.cosmocats.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.cosmocats.repository.NaturalIdRepository;
import org.example.cosmocats.repository.entity.OrderEntity;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public class OrderRepositoryImpl implements NaturalIdRepository<OrderEntity, String> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<OrderEntity> findByNaturalId(String naturalId) {
        return entityManager.unwrap(Session.class)
                .bySimpleNaturalId(OrderEntity.class)
                .loadOptional(naturalId);
    }
}