package com.n8n.service;

import com.n8n.dto.WorkflowExecutionRequest;
import com.n8n.dto.WorkflowExecutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class N8nApiService {

    @Qualifier("n8nWebClient")
    private final WebClient n8nWebClient;

    /**
     * 워크플로우를 실행합니다.
     *
     * @param request 워크플로우 실행 요청
     * @return 실행 결과
     */
    public Mono<WorkflowExecutionResponse> executeWorkflow(WorkflowExecutionRequest request) {
        log.info("Executing n8n workflow: {}", request.getWorkflowId());

        return n8nWebClient
                .post()
                .uri("/api/v1/workflows/{id}/execute", request.getWorkflowId())
                .bodyValue(request.getInputData())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> WorkflowExecutionResponse.builder()
                        .executionId(response.getOrDefault("executionId", "").toString())
                        .status("started")
                        .data(response)
                        .message("Workflow execution started successfully")
                        .build())
                .onErrorResume(error -> {
                    log.error("Error executing workflow: {}", error.getMessage(), error);
                    return Mono.just(WorkflowExecutionResponse.builder()
                            .status("failed")
                            .message("Failed to execute workflow: " + error.getMessage())
                            .build());
                });
    }

    /**
     * 모든 워크플로우 목록을 조회합니다.
     *
     * @return 워크플로우 목록
     */
    @SuppressWarnings("unchecked")
    public Mono<List<Map<String, Object>>> getWorkflows() {
        log.info("Fetching all workflows");

        return n8nWebClient
                .get()
                .uri("/api/v1/workflows")
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Object data = response.get("data");
                    if (data instanceof List) {
                        return (List<Map<String, Object>>) data;
                    }
                    return (List<Map<String, Object>>) (List<?>) List.of();
                })
                .onErrorResume(error -> {
                    log.error("Error fetching workflows: {}", error.getMessage(), error);
                    return Mono.just((List<Map<String, Object>>) (List<?>) List.of());
                });
    }

    /**
     * 특정 워크플로우 정보를 조회합니다.
     *
     * @param workflowId 워크플로우 ID
     * @return 워크플로우 정보
     */
    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> getWorkflow(String workflowId) {
        log.info("Fetching workflow: {}", workflowId);

        return n8nWebClient
                .get()
                .uri("/api/v1/workflows/{id}", workflowId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Map<String, Object>) response)
                .onErrorResume(error -> {
                    log.error("Error fetching workflow {}: {}", workflowId, error.getMessage(), error);
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("error", error.getMessage());
                    return Mono.just(errorMap);
                });
    }

    /**
     * 실행 상태를 조회합니다.
     *
     * @param executionId 실행 ID
     * @return 실행 상태
     */
    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> getExecutionStatus(String executionId) {
        log.info("Fetching execution status: {}", executionId);

        return n8nWebClient
                .get()
                .uri("/api/v1/executions/{id}", executionId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Map<String, Object>) response)
                .onErrorResume(error -> {
                    log.error("Error fetching execution status {}: {}", executionId, error.getMessage(), error);
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("error", error.getMessage());
                    return Mono.just(errorMap);
                });
    }

    /**
     * 워크플로우를 활성화/비활성화합니다.
     *
     * @param workflowId 워크플로우 ID
     * @param active     활성화 여부
     * @return 업데이트 결과
     */
    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> toggleWorkflow(String workflowId, boolean active) {
        log.info("Toggling workflow {} to {}", workflowId, active ? "active" : "inactive");

        return n8nWebClient
                .patch()
                .uri("/api/v1/workflows/{id}", workflowId)
                .bodyValue(Map.of("active", active))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Map<String, Object>) response)
                .onErrorResume(error -> {
                    log.error("Error toggling workflow {}: {}", workflowId, error.getMessage(), error);
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("error", error.getMessage());
                    return Mono.just(errorMap);
                });
    }
}
