package com.mohitprasad.urlshortener.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.OffsetDateTime;

@Data
public class CreateUrlRequest {

    @NotBlank(message = "URL must not be blank")
    @URL(message = "Must be a valid URL")
    private String url;

    @Size(min = 3, max = 20, message = "Custom alias must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Custom alias may only contain alphanumeric characters, hyphens, and underscores")
    private String customAlias;

    @Future(message = "Expiry date must be in the future")
    private OffsetDateTime expiresAt;
}
