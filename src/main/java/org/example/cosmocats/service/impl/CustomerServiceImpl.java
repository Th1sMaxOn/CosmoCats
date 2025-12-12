package org.example.cosmocats.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cosmocats.repository.CustomerRepository;
import org.example.cosmocats.repository.entity.CustomerEntity;
import org.example.cosmocats.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerEntity findOrCreate(String email, String fullName) {
        return customerRepository.findByEmail(email)
                .orElseGet(() -> {
                    CustomerEntity newCustomer = new CustomerEntity();
                    newCustomer.setEmail(email);
                    newCustomer.setFullName(fullName);
                    return customerRepository.save(newCustomer);
                });
    }
}