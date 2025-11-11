package com.cosmo.cats.application.impl;

import com.cosmo.cats.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryProductServiceTest {

    private InMemoryProductService svc;

    @BeforeEach
    void setUp() {
        svc = new InMemoryProductService();
    }

    private Product sampleProduct() {
        return new Product(null, "Planetary Sword", "A shiny blade", new BigDecimal("9.99"), "USD", null);
    }

    @Test
    void createAndFindById() {
        Product p = svc.create(sampleProduct());
        assertNotNull(p.getId());
        Optional<Product> found = svc.findById(p.getId());
        assertTrue(found.isPresent());
        assertEquals("Planetary Sword", found.get().getName());
    }

    @Test
    void findAllAndDelete() {
        svc.create(sampleProduct());
        svc.create(sampleProduct());
        List<Product> all = svc.findAll();
        assertTrue(all.size() >= 2);
        UUID id = all.get(0).getId();
        svc.delete(id);
        assertFalse(svc.findById(id).isPresent());
    }

    @Test
    void updateExistingAndThrowOnMissing() {
        Product p = svc.create(sampleProduct());
        UUID id = p.getId();
        p.setName("Renamed");
        Product updated = svc.update(id, p);
        assertEquals("Renamed", updated.getName());

        UUID random = UUID.randomUUID();
        Product other = sampleProduct();
        assertThrows(NoSuchElementException.class, () -> svc.update(random, other));
    }
}