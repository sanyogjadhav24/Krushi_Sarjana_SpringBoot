package me.sanyogjadhav.orderservice.client;

import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.orderservice.dto.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductResponseDTO getProduct(
            Long productId,
            String token
    ) {
        return restClientBuilder
                .baseUrl(productServiceUrl)
                .build()
                .get()
                .uri("/api/products/{id}", productId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(ProductResponseDTO.class);
    }
}