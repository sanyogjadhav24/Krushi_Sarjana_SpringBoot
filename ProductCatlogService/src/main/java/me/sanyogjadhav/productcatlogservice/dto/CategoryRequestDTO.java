
package me.sanyogjadhav.productcatlogservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequestDTO {

    @NotBlank(message = "Category name cannot be empty")
    @Size(min = 3, max = 50,
            message = "Category name must be between 3 and 50 characters")
    private String name;
}
