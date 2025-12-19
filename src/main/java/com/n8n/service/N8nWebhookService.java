package com.n8n.service;

import com.n8n.dto.N8nWebhookRequest;
import com.n8n.dto.N8nWebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class N8nWebhookService {

    @Qualifier("webhookWebClient")
    private final WebClient webhookWebClient;

    /**
     * n8n Webhook으로 요청을 전송합니다.
     *
     * @param request Webhook 요청 정보
     * @return Webhook 응답
     */
    public Mono<N8nWebhookResponse> triggerWebhook(N8nWebhookRequest request) {
        log.info("Triggering n8n webhook: {}", request.getWebhookPath());

        WebClient.RequestBodySpec requestSpec = webhookWebClient
                .method(org.springframework.http.HttpMethod.valueOf(
                        request.getMethod() != null ? request.getMethod() : "POST"))
                .uri(request.getWebhookPath());

        // 헤더 추가
        if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
            request.getHeaders().forEach(requestSpec::header);
        }

        return requestSpec
                .bodyValue(request.getData() != null ? request.getData() : new HashMap<>())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> N8nWebhookResponse.builder()
                        .success(true)
                        .message("Webhook triggered successfully")
                        .data(response)
                        .build())
                .onErrorResume(error -> {
                    log.error("Error triggering webhook: {}", error.getMessage(), error);
                    return Mono.just(N8nWebhookResponse.builder()
                            .success(false)
                            .message("Failed to trigger webhook: " + error.getMessage())
                            .build());
                });
    }

    /**
     * n8n Webhook을 테스트합니다.
     *
     * @param webhookPath Webhook 경로
     * @return 테스트 결과
     */
    public Mono<N8nWebhookResponse> testWebhook(String webhookPath) {
        log.info("Testing n8n webhook: {}", webhookPath);

        Map<String, Object> testData = new HashMap<>();
        testData.put("test", true);
        testData.put("timestamp", System.currentTimeMillis());
        testData.put("message", "This is a test webhook call");

        N8nWebhookRequest request = N8nWebhookRequest.builder()
                .webhookPath(webhookPath)
                .data(testData)
                .method("POST")
                .build();

        return triggerWebhook(request);
    }
}
