package com.brunomoura.ecommerceapi.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Base64;

@ConfigurationProperties(prefix = "jwt")
@Validated
@Getter
public class JwtProperties {

    @NotBlank
    private String secret;

    @NotNull
    @Min(1)
    private Long expiration;

    public JwtProperties(String secret, Long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public byte[] getSecret() {
        return Base64.getDecoder().decode(this.secret);
    }
}
