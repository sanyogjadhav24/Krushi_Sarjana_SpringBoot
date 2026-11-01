package me.sanyogjadhav.orderservice.security;

public record AuthenticatedUser(
        Long userId,
        UserRole role,
        String email
) {
}