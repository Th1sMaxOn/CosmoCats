package org.example.cosmocats.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Customer {
    Long id;
    String email;
    String fullName;
}