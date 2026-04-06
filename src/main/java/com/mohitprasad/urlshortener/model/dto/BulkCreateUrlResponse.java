package com.mohitprasad.urlshortener.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCreateUrlResponse {

    private List<CreateUrlResponse> results;
    private int successCount;
    private int failureCount;
}
