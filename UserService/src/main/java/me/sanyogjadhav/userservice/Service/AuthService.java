package me.sanyogjadhav.userservice.Service;

import jakarta.transaction.Transactional;
import me.sanyogjadhav.userservice.dto.AuthResDTO;
import me.sanyogjadhav.userservice.dto.LoginReqDTO;
import me.sanyogjadhav.userservice.dto.UserReqDTO;

import me.sanyogjadhav.userservice.dto.UserResDTO;
import me.sanyogjadhav.userservice.model.Role;
import me.sanyogjadhav.userservice.model.User;
import me.sanyogjadhav.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {
    private final UserRepository userrepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Autowired
    AuthService(UserRepository userrepo, PasswordEncoder passwordEncoder, JWTService jwtService){
        this.userrepo=userrepo;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }
    @Transactional
    public UserResDTO register(UserReqDTO reqDTO) {
        String email = reqDTO.getEmail().trim().toLowerCase();

        boolean emailExists = userrepo.existsByEmail(email);
        boolean phoneExists = userrepo.existsByPhoneNo(reqDTO.getPhoneNo());

        if (emailExists && phoneExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Both email and phone number are already registered.");
        }
        if (emailExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email already registered. Please use another email.");
        }
        if (phoneExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Phone number already registered. Please use another phone number.");
        }

        Role role = reqDTO.getRole() == null ? Role.CUSTOMER : reqDTO.getRole();


        User user = new User();
        user.setName(reqDTO.getName());
        user.setEmail(email);
        user.setPhoneNo(reqDTO.getPhoneNo());
        user.setPassword(passwordEncoder.encode(reqDTO.getPassword()));
        user.setRole(role);
        user.setProfilImg(reqDTO.getProfilImg());
        user.setCreatedAt(LocalDateTime.now());
        user.setDeleted(false);

        try {
            return toResponseDto(userrepo.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email or phone number already registered.");
        }
    }

    public AuthResDTO login(LoginReqDTO req) {
        String email = req.getEmail().trim().toLowerCase();

        User user = userrepo.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResDTO(token, "Bearer", jwtService.getExpirationMs(),
                toResponseDto(user));
    }


    private UserResDTO toResponseDto(User user) {
        UserResDTO dto = new UserResDTO();
        dto.setId(user.getUser_id());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNo(user.getPhoneNo());
       dto.setRole(user.getRole());
       dto.setProfilImg(user.getProfilImg());
        return dto;
    }


}
