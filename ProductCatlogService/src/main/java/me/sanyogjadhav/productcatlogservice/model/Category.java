package me.sanyogjadhav.productcatlogservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "Category Name cannot be empty")
    @Column(name="category_name")
    @Size(min = 3, max = 50, message = "Category Name must be between 3 and 50 characters")
    private String name;

}
