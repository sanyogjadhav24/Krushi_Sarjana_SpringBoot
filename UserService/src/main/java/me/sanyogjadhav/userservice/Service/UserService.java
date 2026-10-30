package me.sanyogjadhav.userservice.Service;

import jakarta.validation.Valid;
import me.sanyogjadhav.userservice.dto.UserReqDTO;

import me.sanyogjadhav.userservice.model.User;
import me.sanyogjadhav.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    UserRepository userrepo;

    @Autowired
    UserService(UserRepository userrepo){
        this.userrepo=userrepo;
    }

    public String createUser(UserReqDTO reqDTO) {
        if(reqDTO==null){
            return "Empty User";

        }

    try {
    User user = new User();

    user.setName(reqDTO.getName());
    user.setEmail(reqDTO.getEmail());
    user.setPhoneNo(reqDTO.getPhoneNo());
    user.setPassword(reqDTO.getPassword());
    user.setRole(reqDTO.getRole());
    user.setProfilImg(reqDTO.getProfilImg());
    user.setDeleted(false);
    userrepo.save(user);
    return "User Created Successfully";
    } catch (Exception e){

    return "Unable to Create User: " + e.getMessage();

}

    }
}
