package me.sanyogjadhav.productcatlogservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class InternalServiceAuth {

    @Value("${internal.service.secret}")
    private String expectedSecret;

    public boolean isValid(String providedSecret) {

        if (providedSecret == null || expectedSecret == null) {
            return false;
        }

        return MessageDigest.isEqual(
                expectedSecret.getBytes(StandardCharsets.UTF_8),
                providedSecret.getBytes(StandardCharsets.UTF_8)
        );
    }
}