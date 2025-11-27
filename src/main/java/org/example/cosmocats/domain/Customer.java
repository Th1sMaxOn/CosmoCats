package org.example.cosmocats.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_customers_email", columnNames = "email")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @SequenceGenerator(name = "customer_seq_gen", sequenceName = "customer_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq_gen")
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;
}
