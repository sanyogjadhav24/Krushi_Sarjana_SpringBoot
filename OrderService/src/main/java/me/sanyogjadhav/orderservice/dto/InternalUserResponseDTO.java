package me.sanyogjadhav.orderservice.dto;

public record InternalUserResponseDTO(
        Long userId,
        String name,
        String email,
        String role
) {}