package com.mohitprasad.urlshortener.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohitprasad.urlshortener.model.dto.CreateUrlRequest;
import com.mohitprasad.urlshortener.model.dto.CreateUrlResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UrlShortenerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.autoconfigure.exclude",
                () -> "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                      "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration," +
                      "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration");
        registry.add("spring.cache.type", () -> "none");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndRedirect_fullFlow() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setUrl("https://www.google.com");

        MvcResult result = mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CreateUrlResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CreateUrlResponse.class);

        assertThat(response.getShortCode()).isNotBlank();
        assertThat(response.getOriginalUrl()).isEqualTo("https://www.google.com");

        // Test redirect
        mockMvc.perform(get("/" + response.getShortCode()))
                .andExpect(status().isFound());
    }

    @Test
    void createWithCustomAlias_andRetrieve() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setUrl("https://www.github.com");
        request.setCustomAlias("github-it");

        MvcResult result = mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CreateUrlResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), CreateUrlResponse.class);

        assertThat(response.getShortCode()).isEqualTo("github-it");
    }
}
