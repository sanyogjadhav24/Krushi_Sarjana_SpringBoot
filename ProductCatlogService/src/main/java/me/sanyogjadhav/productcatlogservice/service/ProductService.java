
package me.sanyogjadhav.productcatlogservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.productcatlogservice.dto.NotificationEvent;
import me.sanyogjadhav.productcatlogservice.dto.ProductRequestDTO;
import me.sanyogjadhav.productcatlogservice.dto.ProductResponseDTO;
import me.sanyogjadhav.productcatlogservice.model.Category;
import me.sanyogjadhav.productcatlogservice.model.Product;
import me.sanyogjadhav.productcatlogservice.model.UserRole;
import me.sanyogjadhav.productcatlogservice.repository.CategoryRepository;
import me.sanyogjadhav.productcatlogservice.repository.ProductRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaEventProducer kafkaEventProducer;

    // Add Product
    @Transactional
    public ProductResponseDTO addProduct(
            ProductRequestDTO request,
            Long userId,
            UserRole userRole) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Category Not Found"));

        Product product = new Product();

        product.setName(request.getName());
        product.setCategory(category);
        product.setProductImg(request.getProductImg());
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());

        product.setCreatedBy(userId);
        product.setCreatedByRole(userRole);

        Product savedProduct = productRepository.save(product);

        NotificationEvent event = new NotificationEvent(
                UUID.randomUUID().toString(),
                "PRODUCT_CREATED",
                1,
                Instant.now(),
                "ProductCatalogService",
                Map.of(
                        "productId", savedProduct.getId(),
                        "productName", savedProduct.getName(),
                        "userId", savedProduct.getCreatedBy(),
                        "role", savedProduct.getCreatedByRole().name()
                )
        );

        publishAfterCommit(event);

        return mapToResponse(savedProduct);
    }

    // Get All Products
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts(UserRole userRole) {

        UserRole visibleRole = getVisibleProductRole(userRole);

        return productRepository.findByCreatedByRole(visibleRole)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Product By Id
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(
            Long productId,
            UserRole userRole) {

        UserRole visibleRole = getVisibleProductRole(userRole);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found"));

        if (product.getCreatedByRole() != visibleRole) {
            throw new EntityNotFoundException("Product Not Found");
        }

        return mapToResponse(product);
    }

    // Update Product
    @Transactional
    public ProductResponseDTO updateProduct(
            Long productId,
            ProductRequestDTO request,
            Long userId,
            UserRole userRole) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found"));

        checkOwnership(product, userId, userRole);

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

    // Delete Product
    @Transactional
    public void deleteProduct(
            Long productId,
            Long userId,
            UserRole userRole) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found"));

        checkOwnership(product, userId, userRole);

        productRepository.delete(product);
    }

    // Get Products By Category
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

    // Reserve Stock
    @Transactional
    public void reserveStock(Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        int updatedRows = productRepository.reserveStock(
                productId, quantity
        );

        if (updatedRows == 0) {

            if (!productRepository.existsById(productId)) {
                throw new EntityNotFoundException("Product not found");
            }

            throw new IllegalArgumentException(
                    "Insufficient stock available"
            );
        }
    }

    // Release Stock
    @Transactional
    public void releaseStock(Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        int updatedRows = productRepository.releaseStock(
                productId, quantity
        );

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Product not found");
        }
    }

    // Create Event
    private NotificationEvent createEvent(
            String eventType,
            Map<String, Object> data) {

        return new NotificationEvent(
                UUID.randomUUID().toString(),
                eventType,
                1,
                Instant.now(),
                "ProductCatalogService",
                data
        );
    }

    // Publish After Database Commit
    private void publishAfterCommit(NotificationEvent event) {

        if (TransactionSynchronizationManager.isSynchronizationActive()) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            kafkaEventProducer.publish(
                                    "product-events", event
                            );
                        }
                    }
            );

        } else {
            kafkaEventProducer.publish("product-events", event);
        }
    }

    // Check Ownership
    private void checkOwnership(
            Product product,
            Long userId,
            UserRole userRole) {

        if (!product.getCreatedBy().equals(userId)
                || product.getCreatedByRole() != userRole) {

            throw new AccessDeniedException(
                    "You are not authorized to modify this product");
        }
    }

    // Role Based Visibility
    private UserRole getVisibleProductRole(UserRole userRole) {

        return switch (userRole) {
            case CUSTOMER -> UserRole.FARMER;
            case FARMER -> UserRole.SELLER;
            case SELLER -> UserRole.FARMER;
        };
    }

    // Response DTO
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
