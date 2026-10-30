package me.sanyogjadhav.userservice.controller;

import jakarta.validation.Valid;
import me.sanyogjadhav.userservice.dto.UserReqDTO;
import me.sanyogjadhav.userservice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserReqDTO userReqDTO) {
        String responseMessage = userService.createUser(userReqDTO);

        if ("User Created Successfully".equals(responseMessage)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
    }
}
