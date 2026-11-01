package me.sanyogjadhav.productcatlogservice.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.productcatlogservice.dto.ProductRequestDTO;
import me.sanyogjadhav.productcatlogservice.dto.ProductResponseDTO;
import me.sanyogjadhav.productcatlogservice.model.Category;
import me.sanyogjadhav.productcatlogservice.model.Product;
import me.sanyogjadhav.productcatlogservice.model.UserRole;
import me.sanyogjadhav.productcatlogservice.repository.CategoryRepository;
import me.sanyogjadhav.productcatlogservice.repository.ProductRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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


        return mapToResponse(productRepository.save(product));
    }



    //Get All products based on Logged In User

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts(UserRole userRole){
        UserRole visibleRole= getVisibleProductRole(userRole);

        return productRepository.findByCreatedByRole(visibleRole)
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    //Get product By Id

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long productId, UserRole userRole) {

        UserRole visibleRole= getVisibleProductRole(userRole);



        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found"));

        if(product.getCreatedByRole()!=visibleRole){
            throw new EntityNotFoundException("Product Not Found");
        }

        return mapToResponse(product);
    }


    @Transactional
    public ProductResponseDTO updateProduct(
            Long productId,
            ProductRequestDTO request,
            Long userId,
            UserRole userRole) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found"));

        checkOwnership(product, userId,userRole);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setCategory(category);
        product.setProductImg(request.getProductImg());
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());

        return mapToResponse(productRepository.save(product));
    }


    @Transactional
    public void deleteProduct(Long productId, Long userId,UserRole userRole) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found"));

        checkOwnership(product, userId,userRole);

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByCategory(
            Long categoryId,
            UserRole userRole) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found");
        }

        UserRole visibleRole = getVisibleProductRole(userRole);

        return productRepository
                .findByCategory_IdAndCreatedByRole(categoryId, visibleRole)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    //check ownerShip

    private void checkOwnership(Product product, Long userId, UserRole userRole) {

        if (!product.getCreatedBy().equals(userId) || product.getCreatedByRole()!=userRole) {
            throw new AccessDeniedException(
                    "You are not authorized to modify this product");
        }
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
