package me.sanyogjadhav.productcatlogservice.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.productcatlogservice.dto.ProductRequestDTO;
import me.sanyogjadhav.productcatlogservice.dto.ProductResponseDTO;
import me.sanyogjadhav.productcatlogservice.model.Category;
import me.sanyogjadhav.productcatlogservice.model.Product;
import me.sanyogjadhav.productcatlogservice.model.UserRole;
import me.sanyogjadhav.productcatlogservice.repository.CategoryRepository;
import me.sanyogjadhav.productcatlogservice.repository.ProductRepository;
import org.apache.catalina.User;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Add Product

    public ProductResponseDTO addProduct(
            ProductRequestDTO request,
            Long userId,
            UserRole userRole ){

        Category category= categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new EntityNotFoundException("Category Not Found"));


        Product product=new Product();

        product.setName(request.getName());
        product.setCategory(category);
        product.setProductImg(request.getProductImg());
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());

        product.setCreatedBy(userId);
        product.setCreatedByRole(userRole);
        product.setCreatedAt(LocalDateTime.now());

        return mapToResponse(productRepository.save(product));
    }



    //Get All products based on Logged In User

    @Transactional
    public List<ProductResponseDTO> getAllProducts(UserRole userRole){
        UserRole visibleRole= getVisibleProductRole(userRole);

        return productRepository.findByCreatedByRole(visibleRole)
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    //Role based Visibility

    private UserRole getVisibleProductRole(UserRole userRole){
        return switch(userRole){
            case CUSTOMER -> UserRole.FARMER;
            case FARMER -> UserRole.SELLER;
            case SELLER -> UserRole.FARMER;
        };

    }

    //ResponseDTO
    private ProductResponseDTO mapToResponse(Product product) {

        ProductResponseDTO response = new ProductResponseDTO();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setProductImg(product.getProductImg());
        response.setDescription(product.getDescription());
        response.setQuantity(product.getQuantity());
        response.setPrice(product.getPrice());
        response.setCreatedBy(product.getCreatedBy());
        response.setCreatedByRole(product.getCreatedByRole());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }


}
