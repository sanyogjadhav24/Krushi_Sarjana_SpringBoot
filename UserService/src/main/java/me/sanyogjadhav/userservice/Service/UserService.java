package me.sanyogjadhav.userservice.Service;

import jakarta.validation.Valid;
import me.sanyogjadhav.userservice.dto.UserReqDTO;

import me.sanyogjadhav.userservice.dto.UserResDTO;
import me.sanyogjadhav.userservice.model.User;
import me.sanyogjadhav.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    UserRepository userrepo;

    @Autowired
    UserService(UserRepository userrepo){
        this.userrepo=userrepo;
    }

    public String createUser(UserReqDTO reqDTO) {

        if (reqDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User data cannot be empty");
        }

        boolean emailExists = userrepo.existsByEmail(reqDTO.getEmail());
        boolean phoneExists = userrepo.existsByPhoneNo(reqDTO.getPhoneNo());

        if (emailExists && phoneExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Both Email and Phone Number are already registered."
            );
        }
        if (emailExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already registered. Please use another email."
            );
        }
        if (phoneExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Phone number already registered. Please use another phone number."
            );
        }

                try {
                User user = new User();
                user.setName(reqDTO.getName());
                user.setEmail(reqDTO.getEmail());
                user.setPhoneNo(reqDTO.getPhoneNo());
                user.setPassword(reqDTO.getPassword());
                user.setRole(reqDTO.getRole());
                user.setProfilImg(reqDTO.getProfilImg());
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());;
                user.setDeleted(false);
                userrepo.save(user);
                return "User Created Successfully";
                } catch (Exception e){

    return "Unable to Create User: " + e.getMessage();

}

    }



    public List<UserResDTO> getUsers() {
        return userrepo.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UserResDTO getUser(Long id){
        User user = userrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return  toResponseDto(user);
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


    public UserResDTO updateUser(Long id, UserReqDTO reqDto) {
        User user = userrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(reqDto.getName());
        user.setEmail(reqDto.getEmail());
        user.setPhoneNo(reqDto.getPhoneNo());
        user.setRole(reqDto.getRole());
        user.setProfilImg(reqDto.getProfilImg());

        return toResponseDto(userrepo.save(user));
    }
}
