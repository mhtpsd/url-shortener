package com.mohitprasad.urlshortener.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BulkCreateUrlRequest {

    @NotEmpty(message = "URL list must not be empty")
    @Size(max = 100, message = "Cannot process more than 100 URLs at once")
    @Valid
    private List<CreateUrlRequest> urls;
}
