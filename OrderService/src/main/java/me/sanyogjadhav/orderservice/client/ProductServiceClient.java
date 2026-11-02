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

    @Value("${internal.service.secret}")
    private String internalServiceSecret;

    public ProductResponseDTO getProduct(Long productId, String token) {

        String authorizationHeader = token.startsWith("Bearer ")
                ? token
                : "Bearer " + token;

        return restClientBuilder
                .baseUrl(productServiceUrl)
                .build()
                .get()
                .uri("/api/products/{id}", productId)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                )
                .retrieve()
                .body(ProductResponseDTO.class);
    }

    public void reserveStock(Long productId, Integer quantity) {

        restClientBuilder
                .baseUrl(productServiceUrl)
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{id}/reserve-stock")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .header(
                        "X-Internal-Service-Key",
                        internalServiceSecret
                )
                .retrieve()
                .toBodilessEntity();
    }

    public void releaseStock(Long productId, Integer quantity) {

        restClientBuilder
                .baseUrl(productServiceUrl)
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/{id}/release-stock")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .header(
                        "X-Internal-Service-Key",
                        internalServiceSecret
                )
                .retrieve()
                .toBodilessEntity();
    }
}