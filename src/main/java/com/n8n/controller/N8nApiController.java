package com.n8n.controller;

import com.n8n.dto.ApiResponse;
import com.n8n.dto.WorkflowExecutionRequest;
import com.n8n.dto.WorkflowExecutionResponse;
import com.n8n.service.N8nApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/n8n")
@RequiredArgsConstructor
public class N8nApiController {

    private final N8nApiService n8nApiService;

    /**
     * 모든 워크플로우 목록을 조회합니다.
     *
     * GET /api/n8n/workflows
     */
    @GetMapping("/workflows")
    public Mono<ResponseEntity<ApiResponse<List<Map<String, Object>>>>> getWorkflows() {
        log.info("Fetching all workflows");

        return n8nApiService.getWorkflows()
                .map(workflows -> ResponseEntity.ok(
                        ApiResponse.success("Workflows retrieved successfully", workflows)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to fetch workflows: " + error.getMessage()))));
    }

    /**
     * 특정 워크플로우 정보를 조회합니다.
     *
     * GET /api/n8n/workflows/{workflowId}
     */
    @GetMapping("/workflows/{workflowId}")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> getWorkflow(
            @PathVariable String workflowId) {
        log.info("Fetching workflow: {}", workflowId);

        return n8nApiService.getWorkflow(workflowId)
                .map(workflow -> ResponseEntity.ok(
                        ApiResponse.success("Workflow retrieved successfully", workflow)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to fetch workflow: " + error.getMessage()))));
    }

    /**
     * 워크플로우를 실행합니다.
     *
     * POST /api/n8n/workflows/execute
     * Body: {
     *   "workflowId": "workflow-id",
     *   "inputData": { "key": "value" }
     * }
     */
    @PostMapping("/workflows/execute")
    public Mono<ResponseEntity<ApiResponse<WorkflowExecutionResponse>>> executeWorkflow(
            @RequestBody WorkflowExecutionRequest request) {
        log.info("Executing workflow: {}", request.getWorkflowId());

        return n8nApiService.executeWorkflow(request)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success("Workflow execution initiated", response)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to execute workflow: " + error.getMessage()))));
    }

    /**
     * 실행 상태를 조회합니다.
     *
     * GET /api/n8n/executions/{executionId}
     */
    @GetMapping("/executions/{executionId}")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> getExecutionStatus(
            @PathVariable String executionId) {
        log.info("Fetching execution status: {}", executionId);

        return n8nApiService.getExecutionStatus(executionId)
                .map(status -> ResponseEntity.ok(
                        ApiResponse.success("Execution status retrieved", status)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to fetch execution status: " + error.getMessage()))));
    }

    /**
     * 워크플로우를 활성화합니다.
     *
     * PUT /api/n8n/workflows/{workflowId}/activate
     */
    @PutMapping("/workflows/{workflowId}/activate")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> activateWorkflow(
            @PathVariable String workflowId) {
        log.info("Activating workflow: {}", workflowId);

        return n8nApiService.toggleWorkflow(workflowId, true)
                .map(result -> ResponseEntity.ok(
                        ApiResponse.success("Workflow activated", result)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to activate workflow: " + error.getMessage()))));
    }

    /**
     * 워크플로우를 비활성화합니다.
     *
     * PUT /api/n8n/workflows/{workflowId}/deactivate
     */
    @PutMapping("/workflows/{workflowId}/deactivate")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> deactivateWorkflow(
            @PathVariable String workflowId) {
        log.info("Deactivating workflow: {}", workflowId);

        return n8nApiService.toggleWorkflow(workflowId, false)
                .map(result -> ResponseEntity.ok(
                        ApiResponse.success("Workflow deactivated", result)))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(
                                ApiResponse.error("Failed to deactivate workflow: " + error.getMessage()))));
    }
}
