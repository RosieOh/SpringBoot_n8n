package com.n8n.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class N8nWebhookResponse {
    private boolean success;
    private String message;
    private Object data;
    private String executionId;
}
