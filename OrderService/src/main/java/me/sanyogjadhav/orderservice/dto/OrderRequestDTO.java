package me.sanyogjadhav.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    @NotBlank
    private String shippingAddress;

    @NotEmpty
    @Valid
    private List<OrderItemRequestDTO> items;
}