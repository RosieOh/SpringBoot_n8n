package com.n8n.controller;

import com.n8n.dto.ApiResponse;
import com.n8n.dto.N8nWebhookRequest;
import com.n8n.dto.N8nWebhookResponse;
import com.n8n.service.N8nWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/n8n/webhook")
@RequiredArgsConstructor
public class N8nWebhookController {

    private final N8nWebhookService webhookService;

    /**
     * n8n Webhook을 호출합니다.
     *
     * POST /api/n8n/webhook/trigger
     * Body: {
     *   "webhookPath": "/your-webhook-path",
     *   "data": { "key": "value" },
     *   "headers": { "Custom-Header": "value" },
     *   "method": "POST"
     * }
     */
    @PostMapping("/trigger")
    public Mono<ResponseEntity<ApiResponse<N8nWebhookResponse>>> triggerWebhook(
            @RequestBody N8nWebhookRequest request) {
        log.info("Received webhook trigger request for path: {}", request.getWebhookPath());

        return webhookService.triggerWebhook(request)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success("Webhook triggered", response)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to trigger webhook: " + error.getMessage()))));
    }

    /**
     * n8n Webhook을 테스트합니다.
     *
     * GET /api/n8n/webhook/test/{webhookPath}
     */
    @GetMapping("/test/{*webhookPath}")
    public Mono<ResponseEntity<ApiResponse<N8nWebhookResponse>>> testWebhook(
            @PathVariable String webhookPath) {
        log.info("Testing webhook: {}", webhookPath);

        return webhookService.testWebhook(webhookPath)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success("Webhook test completed", response)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to test webhook: " + error.getMessage()))));
    }

    /**
     * n8n Webhook을 간단하게 호출 (경로만 지정)
     *
     * POST /api/n8n/webhook/simple/{webhookPath}
     * Body: { "any": "data" }
     */
    @PostMapping("/simple/{*webhookPath}")
    public Mono<ResponseEntity<ApiResponse<N8nWebhookResponse>>> simpleWebhookTrigger(
            @PathVariable String webhookPath,
            @RequestBody(required = false) Map<String, Object> data) {
        log.info("Simple webhook trigger for path: {}", webhookPath);

        N8nWebhookRequest request = N8nWebhookRequest.builder()
                .webhookPath(webhookPath)
                .data(data)
                .method("POST")
                .build();

        return webhookService.triggerWebhook(request)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success("Webhook triggered", response)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to trigger webhook: " + error.getMessage()))));
    }
}
