package com.flower.member.dto;

public record AddressDto(
    Long id,
    String recipientName,
    String recipientPhone,
    String zipCode,
    String street,
    String city,
    boolean isDefault
) {}
