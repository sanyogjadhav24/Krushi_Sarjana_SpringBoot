package me.sanyogjadhav.productcatlogservice.security;

import me.sanyogjadhav.productcatlogservice.model.UserRole;

public record AuthenticatedUser(
        Long userId,
        UserRole role,
        String email
    ) {

}
