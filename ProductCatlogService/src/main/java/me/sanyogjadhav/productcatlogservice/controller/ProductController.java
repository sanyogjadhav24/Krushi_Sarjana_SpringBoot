package me.sanyogjadhav.productcatlogservice.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import me.sanyogjadhav.productcatlogservice.dto.ProductRequestDTO;
import me.sanyogjadhav.productcatlogservice.dto.ProductResponseDTO;

import me.sanyogjadhav.productcatlogservice.security.AuthenticatedUser;
import me.sanyogjadhav.productcatlogservice.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Add Product
    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(

            @AuthenticationPrincipal AuthenticatedUser user,

            @Valid @RequestBody ProductRequestDTO request) {

        ProductResponseDTO response =
                productService.addProduct(
                        request,
                        user.userId(),
                        user.role()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get All Products
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(

            @AuthenticationPrincipal AuthenticatedUser user) {

        List<ProductResponseDTO> products =
                productService.getAllProducts(user.role());

        return ResponseEntity.ok(products);
    }
}
