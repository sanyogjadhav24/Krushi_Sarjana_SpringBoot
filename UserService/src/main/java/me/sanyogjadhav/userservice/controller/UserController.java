package me.sanyogjadhav.userservice.controller;

import jakarta.validation.Valid;
import me.sanyogjadhav.userservice.dto.UserReqDTO;
import me.sanyogjadhav.userservice.Service.UserService;
import me.sanyogjadhav.userservice.dto.UserResDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<UserResDTO>> getUsers(){

       List<UserResDTO> users= userService.getUsers();

       if(users==null){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
       }

       return ResponseEntity.status(HttpStatus.FOUND).body(users);

    }

    @GetMapping("{id}")
    public ResponseEntity<UserResDTO> getUser(@PathVariable Long id){
        UserResDTO user= userService.getUser(id);

        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(user);
    }
}
