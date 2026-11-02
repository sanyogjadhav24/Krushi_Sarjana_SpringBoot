package me.sanyogjadhav.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.userservice.dto.AuthResDTO;
import me.sanyogjadhav.userservice.dto.LoginReqDTO;
import me.sanyogjadhav.userservice.dto.UserReqDTO;
import me.sanyogjadhav.userservice.Service.AuthService;
import me.sanyogjadhav.userservice.dto.UserResDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    AuthController(AuthService authService) {
        this.authService = authService;

    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResDTO register(@Valid @RequestBody UserReqDTO reqDTO) {
        return authService.register(reqDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully")
        );
    }

    @PostMapping("/login")
    public AuthResDTO login(@Valid @RequestBody LoginReqDTO reqDTO) {
        return authService.login(reqDTO);
    }
}

