package me.sanyogjadhav.userservice.repository;


import me.sanyogjadhav.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
