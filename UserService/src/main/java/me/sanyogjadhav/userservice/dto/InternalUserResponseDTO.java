package me.sanyogjadhav.userservice.dto;

import me.sanyogjadhav.userservice.model.Role;

public record InternalUserResponseDTO(
        Long userId,
        String name,
        String email,
        Role role
) {}