
package me.sanyogjadhav.productcatlogservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "Product name cannot be empty")
    @Size(min = 3, max = 50,
            message = "Product name must be between 3 and 50 characters")
    private String name;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    @NotBlank(message = "Product image URL cannot be empty")
    private String productImg;

    @NotBlank(message = "Product description cannot be empty")
    @Size(min = 10, max = 500,
            message = "Description must be between 10 and 500 characters")
    private String description;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;
}
