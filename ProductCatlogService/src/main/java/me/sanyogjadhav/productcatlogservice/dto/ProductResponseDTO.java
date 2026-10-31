
package me.sanyogjadhav.productcatlogservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseDTO {

    private Long id;

    private String name;

    private Long categoryId;

    private String categoryName;

    private String productImg;

    private String description;

    private int quantity;

    private BigDecimal price;

    private Long createdBy;

    private String createdByRole;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
