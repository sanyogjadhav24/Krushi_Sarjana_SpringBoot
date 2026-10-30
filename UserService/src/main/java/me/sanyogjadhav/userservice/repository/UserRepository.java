package me.sanyogjadhav.userservice.repository;

import me.sanyogjadhav.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // Use standard java.util.Optional

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNo(String phoneNo);
    List<User> findByIsDeletedFalse();
    Optional<User> findByIdAndIsDeletedFalse(Long id);
    Optional<User> findByEmailAndIsDeletedFalse(String email);

}