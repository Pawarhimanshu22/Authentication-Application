package com.himanshu.auth_backend.dtos.Response_DTOs;

import com.himanshu.auth_backend.entities.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

// Use this for sending address data back to client
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDto {

    private UUID id;
    private String addressLine1;
    private String addressLine2;
    private String street;
    private String buildingNumber;
    private String buildingName;
    private String floor;
    private String apartmentNumber;
    private String landmark;
    private String area;
    private String city;
    private String district;
    private String state;
    private String country;
    private String zipCode;
    private String postalCode;
    private AddressType addressType;
    private String label;
    private Instant createdAt;
    private Instant updatedAt;
}