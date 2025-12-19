package com.n8n.controller;

import com.n8n.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * n8n 워크플로우에서 호출할 수 있는 공개 API 엔드포인트
 * Security 설정에서 /api/public/** 경로는 인증 없이 접근 가능
 */
@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicApiController {

    /**
     * 간단한 데이터 처리 API
     * n8n의 HTTP Request 노드에서 호출 가능
     *
     * POST /api/public/process
     * Body: { "data": "your data" }
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processData(
            @RequestBody Map<String, Object> request) {
        log.info("Processing data from n8n: {}", request);

        Map<String, Object> result = new HashMap<>();
        result.put("originalData", request);
        result.put("processedAt", LocalDateTime.now());
        result.put("status", "processed");
        result.put("message", "Data processed successfully");

        // 여기에 실제 비즈니스 로직 추가
        if (request.containsKey("data")) {
            String data = request.get("data").toString();
            result.put("dataLength", data.length());
            result.put("dataUpperCase", data.toUpperCase());
        }

        return ResponseEntity.ok(ApiResponse.success("Data processed", result));
    }

    /**
     * 사용자 정보 생성 API
     *
     * POST /api/public/users
     * Body: { "name": "John", "email": "john@example.com" }
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createUser(
            @RequestBody Map<String, Object> userData) {
        log.info("Creating user from n8n: {}", userData);

        Map<String, Object> user = new HashMap<>(userData);
        user.put("id", System.currentTimeMillis());
        user.put("createdAt", LocalDateTime.now());
        user.put("status", "active");

        return ResponseEntity.ok(ApiResponse.success("User created", user));
    }

    /**
     * 사용자 정보 조회 API
     *
     * GET /api/public/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUser(
            @PathVariable String userId) {
        log.info("Fetching user from n8n: {}", userId);

        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("name", "Sample User");
        user.put("email", "sample@example.com");
        user.put("status", "active");
        user.put("retrievedAt", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success("User retrieved", user));
    }

    /**
     * 데이터 검증 API
     *
     * POST /api/public/validate
     * Body: { "field": "value" }
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateData(
            @RequestBody Map<String, Object> data) {
        log.info("Validating data from n8n: {}", data);

        Map<String, Object> validation = new HashMap<>();
        validation.put("isValid", true);
        validation.put("checkedAt", LocalDateTime.now());
        validation.put("fieldCount", data.size());

        // 간단한 검증 로직 예시
        if (data.containsKey("email")) {
            String email = data.get("email").toString();
            boolean isValidEmail = email.contains("@") && email.contains(".");
            validation.put("emailValid", isValidEmail);
        }

        return ResponseEntity.ok(ApiResponse.success("Validation completed", validation));
    }

    /**
     * 계산 API
     *
     * POST /api/public/calculate
     * Body: { "operation": "add", "num1": 10, "num2": 5 }
     */
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculate(
            @RequestBody Map<String, Object> request) {
        log.info("Calculating from n8n: {}", request);

        String operation = request.getOrDefault("operation", "add").toString();
        double num1 = Double.parseDouble(request.getOrDefault("num1", 0).toString());
        double num2 = Double.parseDouble(request.getOrDefault("num2", 0).toString());

        double result = switch (operation.toLowerCase()) {
            case "add" -> num1 + num2;
            case "subtract" -> num1 - num2;
            case "multiply" -> num1 * num2;
            case "divide" -> num2 != 0 ? num1 / num2 : 0;
            default -> 0;
        };

        Map<String, Object> response = new HashMap<>();
        response.put("operation", operation);
        response.put("num1", num1);
        response.put("num2", num2);
        response.put("result", result);
        response.put("calculatedAt", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success("Calculation completed", response));
    }

    /**
     * 헬스 체크 API
     *
     * GET /api/public/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "n8n-integration-api");

        return ResponseEntity.ok(ApiResponse.success("Service is healthy", health));
    }

    /**
     * Webhook 수신 엔드포인트
     * n8n의 Webhook 노드에서 데이터를 전송받을 수 있음
     *
     * POST /webhook/receive
     */
    @PostMapping("/webhook/receive")
    public ResponseEntity<ApiResponse<Map<String, Object>>> receiveWebhook(
            @RequestBody(required = false) Map<String, Object> webhookData,
            @RequestHeader Map<String, String> headers) {
        log.info("Received webhook from n8n");
        log.info("Headers: {}", headers);
        log.info("Data: {}", webhookData);

        Map<String, Object> response = new HashMap<>();
        response.put("received", true);
        response.put("timestamp", LocalDateTime.now());
        response.put("dataReceived", webhookData);
        response.put("headersReceived", headers.size());

        return ResponseEntity.ok(ApiResponse.success("Webhook received", response));
    }
}
