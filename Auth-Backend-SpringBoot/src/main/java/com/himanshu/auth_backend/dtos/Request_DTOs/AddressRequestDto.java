package com.himanshu.auth_backend.dtos.Request_DTOs;

import com.himanshu.auth_backend.entities.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


// Use this for both CREATE and UPDATE operations
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDto {

    @Size(max = 500, message = "Address line 1 must not exceed 500 characters")
    private String addressLine1;

    @Size(max = 500, message = "Address line 2 must not exceed 500 characters")
    private String addressLine2;

    @Size(max = 255, message = "Street must not exceed 255 characters")
    private String street;

    @Size(max = 50, message = "Building number must not exceed 50 characters")
    private String buildingNumber;

    @Size(max = 255, message = "Building name must not exceed 255 characters")
    private String buildingName;

    @Size(max = 50, message = "Floor must not exceed 50 characters")
    private String floor;

    @Size(max = 50, message = "Apartment number must not exceed 50 characters")
    private String apartmentNumber;

    @Size(max = 255, message = "Landmark must not exceed 255 characters")
    private String landmark;

    @Size(max = 255, message = "Area must not exceed 255 characters")
    private String area;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    private String zipCode;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    private AddressType addressType;

    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;
}