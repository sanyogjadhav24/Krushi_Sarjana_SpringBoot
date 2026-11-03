package me.sanyogjadhav.userservice.controller;

import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.userservice.dto.InternalUserResponseDTO;
import me.sanyogjadhav.userservice.security.InternalServiceAuth;
import me.sanyogjadhav.userservice.Service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;
    private final InternalServiceAuth internalServiceAuth;

    @GetMapping("/{userId}")
    public ResponseEntity<InternalUserResponseDTO> getUserById(
            @PathVariable Long userId,
            @RequestHeader(
                    value = "X-Internal-Service-Key",
                    required = false
            ) String secret
    ) {

        if (!internalServiceAuth.isValid(secret)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid internal service key"
            );
        }

        return ResponseEntity.ok(
                userService.getInternalUserById(userId)
        );
    }
}