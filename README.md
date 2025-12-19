# n8n Integration API

Spring Boot 기반의 n8n 자동화 워크플로우 통합 API입니다. n8n과의 양방향 통합을 제공합니다.

## 주요 기능

### 1. n8n Webhook 트리거 호출
- n8n 워크플로우의 Webhook 트리거를 호출
- 외부에서 HTTP 요청으로 n8n 워크플로우 실행

### 2. n8n REST API 통합
- n8n의 공식 API를 사용하여 워크플로우 관리
- 워크플로우 조회, 실행, 활성화/비활성화

### 3. n8n에서 호출 가능한 공개 API
- n8n 워크플로우에서 이 API를 호출
- 다양한 비즈니스 로직 처리 엔드포인트 제공

## 시작하기

### 필요 사항
- Java 21
- Gradle 7.x+
- n8n 인스턴스 (로컬 또는 클라우드)

### 설치 및 실행

1. 프로젝트 클론 및 설정
```bash
cd n8n
```

2. application.properties 설정
```properties
# n8n 서버 URL 설정 (기본값: http://localhost:5678)
n8n.base-url=http://localhost:5678

# n8n API 키 설정
n8n.api-key=your-n8n-api-key-here

# Webhook 베이스 URL
n8n.webhook.base-url=${n8n.base-url}/webhook
```

3. 애플리케이션 빌드
```bash
./gradlew build
```

4. 애플리케이션 실행
```bash
./gradlew bootRun
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

## API 사용 가이드

### 1. n8n Webhook 트리거 호출

#### Webhook 호출 (상세 설정)
```bash
curl -X POST http://localhost:8080/api/n8n/webhook/trigger \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "webhookPath": "/your-webhook-id",
    "data": {
      "message": "Hello from API",
      "timestamp": "2024-01-01T00:00:00"
    },
    "method": "POST"
  }'
```

#### Webhook 간단 호출
```bash
curl -X POST http://localhost:8080/api/n8n/webhook/simple/your-webhook-id \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "message": "Hello from API"
  }'
```

#### Webhook 테스트
```bash
curl -X GET http://localhost:8080/api/n8n/webhook/test/your-webhook-id \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

### 2. n8n REST API 통합

#### 모든 워크플로우 조회
```bash
curl -X GET http://localhost:8080/api/n8n/workflows \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

#### 특정 워크플로우 조회
```bash
curl -X GET http://localhost:8080/api/n8n/workflows/{workflowId} \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

#### 워크플로우 실행
```bash
curl -X POST http://localhost:8080/api/n8n/workflows/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "workflowId": "your-workflow-id",
    "inputData": {
      "key": "value"
    }
  }'
```

#### 실행 상태 조회
```bash
curl -X GET http://localhost:8080/api/n8n/executions/{executionId} \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

#### 워크플로우 활성화/비활성화
```bash
# 활성화
curl -X PUT http://localhost:8080/api/n8n/workflows/{workflowId}/activate \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="

# 비활성화
curl -X PUT http://localhost:8080/api/n8n/workflows/{workflowId}/deactivate \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

### 3. n8n에서 호출 가능한 공개 API

이 엔드포인트들은 **인증 없이** 접근 가능하며, n8n 워크플로우의 HTTP Request 노드에서 호출할 수 있습니다.

#### 데이터 처리
```bash
curl -X POST http://localhost:8080/api/public/process \
  -H "Content-Type: application/json" \
  -d '{
    "data": "sample data"
  }'
```

**n8n HTTP Request 노드 설정:**
- Method: POST
- URL: `http://localhost:8080/api/public/process`
- Body: JSON
- JSON: `{"data": "{{ $json.yourField }}"}`

#### 사용자 생성
```bash
curl -X POST http://localhost:8080/api/public/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com"
  }'
```

#### 사용자 조회
```bash
curl -X GET http://localhost:8080/api/public/users/123
```

#### 데이터 검증
```bash
curl -X POST http://localhost:8080/api/public/validate \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "field": "value"
  }'
```

#### 계산
```bash
curl -X POST http://localhost:8080/api/public/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "add",
    "num1": 10,
    "num2": 5
  }'
```

지원되는 연산: `add`, `subtract`, `multiply`, `divide`

#### Webhook 수신
```bash
curl -X POST http://localhost:8080/api/public/webhook/receive \
  -H "Content-Type: application/json" \
  -d '{
    "event": "user.created",
    "data": {
      "userId": 123
    }
  }'
```

#### 헬스 체크
```bash
curl -X GET http://localhost:8080/api/public/health
```

## n8n 워크플로우 예시

### 예시 1: n8n에서 이 API 호출하기

1. n8n에서 새 워크플로우 생성
2. **HTTP Request** 노드 추가
3. 설정:
   - Method: POST
   - URL: `http://localhost:8080/api/public/process`
   - Body Content Type: JSON
   - Specify Body: Using JSON
   - JSON:
   ```json
   {
     "data": "{{ $json.message }}"
   }
   ```

### 예시 2: 이 API에서 n8n Webhook 호출하기

1. n8n에서 새 워크플로우 생성
2. **Webhook** 노드 추가
3. Webhook URL 복사 (예: `http://localhost:5678/webhook/abc123`)
4. 이 API를 사용하여 호출:
   ```bash
   curl -X POST http://localhost:8080/api/n8n/webhook/simple/abc123 \
     -H "Content-Type: application/json" \
     -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
     -d '{"message": "Hello from API"}'
   ```

### 예시 3: 워크플로우 실행

1. n8n에서 워크플로우 생성
2. 워크플로우 ID 확인
3. API를 통해 실행:
   ```bash
   curl -X POST http://localhost:8080/api/n8n/workflows/execute \
     -H "Content-Type: application/json" \
     -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
     -d '{
       "workflowId": "your-workflow-id",
       "inputData": {"key": "value"}
     }'
   ```

## 프로젝트 구조

```
src/main/java/com/n8n/
├── N8nApplication.java           # 메인 애플리케이션
├── config/
│   ├── N8nConfig.java           # n8n 설정 및 WebClient Bean
│   └── SecurityConfig.java      # Spring Security 설정
├── dto/
│   ├── ApiResponse.java         # 공통 API 응답 DTO
│   ├── N8nWebhookRequest.java   # Webhook 요청 DTO
│   ├── N8nWebhookResponse.java  # Webhook 응답 DTO
│   ├── WorkflowExecutionRequest.java   # 워크플로우 실행 요청 DTO
│   └── WorkflowExecutionResponse.java  # 워크플로우 실행 응답 DTO
├── service/
│   ├── N8nWebhookService.java   # Webhook 호출 서비스
│   └── N8nApiService.java       # n8n API 통합 서비스
└── controller/
    ├── N8nWebhookController.java  # Webhook 관련 엔드포인트
    ├── N8nApiController.java      # n8n API 관련 엔드포인트
    └── PublicApiController.java   # n8n에서 호출 가능한 공개 API
```

## 보안

- 기본 인증: `admin/admin123` (프로덕션에서는 반드시 변경!)
- `/api/public/**`와 `/webhook/**` 경로는 인증 없이 접근 가능
- 다른 모든 엔드포인트는 Basic Authentication 필요
- CSRF 보호는 비활성화 (REST API이므로)

## n8n API 키 설정 방법

1. n8n 웹 인터페이스 접속
2. Settings > API 이동
3. API 키 생성
4. `application.properties`의 `n8n.api-key`에 설정

## 문제 해결

### n8n 연결 오류
- n8n 서버가 실행 중인지 확인
- `application.properties`의 `n8n.base-url`이 올바른지 확인
- n8n API가 활성화되어 있는지 확인

### Webhook 호출 실패
- Webhook URL이 올바른지 확인
- n8n 워크플로우가 활성화되어 있는지 확인
- Webhook 트리거가 "Listening" 상태인지 확인

### 인증 오류
- Basic Auth 헤더가 올바른지 확인
- 사용자 이름/비밀번호가 `application.properties`와 일치하는지 확인

## 라이센스

이 프로젝트는 학습 및 개발 목적으로 제공됩니다.

## 기여

이슈와 풀 리퀘스트를 환영합니다!
