package org.example.cosmocats.repository;

import java.io.Serializable;
import java.util.Optional;

public interface NaturalIdRepository<T, ID extends Serializable> {
    Optional<T> findByNaturalId(ID naturalId);
}