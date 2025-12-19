package com.n8n.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Data
@Configuration
@ConfigurationProperties(prefix = "n8n")
public class N8nConfig {

    private String baseUrl;
    private String apiKey;
    private WebhookConfig webhook = new WebhookConfig();

    @Data
    public static class WebhookConfig {
        private String baseUrl;
    }

    @Bean
    public WebClient n8nWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-N8N-API-KEY", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient webhookWebClient() {
        return WebClient.builder()
                .baseUrl(webhook.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
