package me.sanyogjadhav.userservice.dto;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import me.sanyogjadhav.userservice.model.Role;

public class UserResDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNo;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profilImg;
   // private Boolean isDeleted;

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProfilImg() {
        return profilImg;
    }

    public void setProfilImg(String profilImg) {
        this.profilImg = profilImg;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
