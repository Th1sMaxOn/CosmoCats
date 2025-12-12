package org.example.cosmocats.mapper;

import org.example.cosmocats.domain.Customer;
import org.example.cosmocats.repository.entity.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toDomain(CustomerEntity entity);
    CustomerEntity toEntity(Customer domain);
}