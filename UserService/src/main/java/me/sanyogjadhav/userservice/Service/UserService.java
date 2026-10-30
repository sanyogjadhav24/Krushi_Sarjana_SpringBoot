package me.sanyogjadhav.userservice.Service;
import me.sanyogjadhav.userservice.dto.UserReqDTO;
import me.sanyogjadhav.userservice.dto.UserResDTO;
import me.sanyogjadhav.userservice.model.User;
import me.sanyogjadhav.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userrepo;

    public UserService(UserRepository userrepo) {
        this.userrepo = userrepo;
    }


    public List<UserResDTO> getUsers() {
        return userrepo.findByIsDeletedFalse()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    // Get one by id (non-deleted only)
    public UserResDTO getUserById(Long id) {
        return toResponseDto(findActiveUser(id));
    }

    // Update user
    @Transactional
    public UserResDTO updateUser(Long id, UserReqDTO dto) {
        User user = findActiveUser(id);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return toResponseDto(userrepo.save(user));
    }

    // Soft delete: marks the row as deleted
    @Transactional
    public void softDeleteUser(Long id) {
        User user = findActiveUser(id);
        user.setDeleted(true);
        userrepo.save(user);
    }

    // Hard delete: removes the row from the database
    @Transactional
    public void deleteUser(Long id) {
        if (!userrepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userrepo.deleteById(id);
    }

    private User findActiveUser(Long id) {
        return userrepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    private UserResDTO toResponseDto(User user) {
        UserResDTO dto = new UserResDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setProfilImg(user.getProfilImg());
        dto.setRole(user.getRole());

        return dto;
    }
}
