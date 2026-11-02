package me.sanyogjadhav.orderservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Long createdBy;
    private String createdByRole;
}