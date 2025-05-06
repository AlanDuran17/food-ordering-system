package com.alanduran.order.service.domain.valueobject;

import lombok.Data;

import java.util.UUID;

@Data
public class StreetAddress {
    private final UUID id;
    private final String street;
    private final String postalCode;
    private final String city;
}
