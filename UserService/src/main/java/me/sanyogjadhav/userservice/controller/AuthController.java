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

    @PostMapping("/login")
    public AuthResDTO login(@Valid @RequestBody LoginReqDTO reqDTO) {
        return authService.login(reqDTO);
    }
}


//
//
//    @GetMapping("{id}")
//    public ResponseEntity<UserResDTO> getUser(@PathVariable Long id){
//        UserResDTO user= userService.getUser(id);
//
//        if(user==null){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        return ResponseEntity.status(HttpStatus.FOUND).body(user);
//    }
//
//    @PutMapping("{id}")
//    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserReqDTO reqDto){
//        UserResDTO updatedUser= userService.updateUser(id,reqDto);
//
//        return ResponseEntity.status(HttpStatus.OK).body("User Updated Successfully");
//    }
//
//@DeleteMapping("delete/{id}")
//public ResponseEntity<String> deleteUserPermanently(@PathVariable Long id){
//    String deletedUser = userService.deleteUserPermanently(id);
//
//    return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
//}
//
//@DeleteMapping("{id}")
//public ResponseEntity<String> deleteUser(@PathVariable Long id){
//
//    String deletedUser= userService.deleteUser(id);
//
//    return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
//
//}

