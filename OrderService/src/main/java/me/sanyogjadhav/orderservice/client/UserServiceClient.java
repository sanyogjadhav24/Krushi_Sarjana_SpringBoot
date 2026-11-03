package me.sanyogjadhav.orderservice.client;

import lombok.RequiredArgsConstructor;
import me.sanyogjadhav.orderservice.dto.InternalUserResponseDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${internal.service.secret}")
    private String internalServiceSecret;

    public InternalUserResponseDTO getUserById(Long userId) {

        return restClientBuilder
                .baseUrl(userServiceUrl)
                .build()
                .get()
                .uri("/internal/users/{userId}", userId)
                .header(
                        "X-Internal-Service-Key",
                        internalServiceSecret
                )
                .retrieve()
                .body(InternalUserResponseDTO.class);
    }
}