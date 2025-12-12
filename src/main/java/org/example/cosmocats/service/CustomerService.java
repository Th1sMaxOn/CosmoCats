package org.example.cosmocats.service;

import org.example.cosmocats.repository.entity.CustomerEntity;

public interface CustomerService {
    CustomerEntity findOrCreate(String email, String fullName);
}