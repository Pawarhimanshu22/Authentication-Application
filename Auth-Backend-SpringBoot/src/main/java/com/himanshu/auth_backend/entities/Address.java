package com.himanshu.auth_backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "address_line1", nullable = false, length = 500)
    private String addressLine1;

    @Column(name = "address_line2", length = 500)
    private String addressLine2;

    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "building_number", length = 50)
    private String buildingNumber;

    @Column(name = "building_name", length = 255)
    private String buildingName;

    @Column(name = "floor", length = 50)
    private String floor;

    @Column(name = "apartment_number", length = 50)
    private String apartmentNumber;


    @Column(name = "landmark", length = 255)
    private String landmark;

    @Column(name = "area", length = 255)
    private String area;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @Column(name = "postal_code", length = 20)
    private String postalCode;


    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 50)
    @Builder.Default
    private AddressType addressType = AddressType.HOME;

    @Column(name = "label", length = 100)
    private String label;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();


    @PrePersist
    protected void prePersist()
    {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate()
    {
        updatedAt = Instant.now();
    }
}