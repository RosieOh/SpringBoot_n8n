# 빠른 시작 가이드

## 5분 안에 시작하기

### 1단계: n8n 설치 및 실행

```bash
# Docker로 n8n 실행 (권장)
docker run -it --rm \
  --name n8n \
  -p 5678:5678 \
  -e N8N_BASIC_AUTH_ACTIVE=true \
  -e N8N_BASIC_AUTH_USER=admin \
  -e N8N_BASIC_AUTH_PASSWORD=admin \
  n8nio/n8n

# 또는 npm으로 설치
npm install -g n8n
n8n start
```

n8n이 `http://localhost:5678`에서 실행됩니다.

### 2단계: n8n API 키 생성

1. 브라우저에서 `http://localhost:5678` 접속
2. 로그인 (admin/admin)
3. Settings > API 이동
4. "Create new API key" 클릭
5. API 키 복사

### 3단계: Spring Boot 애플리케이션 설정

`src/main/resources/application.properties` 파일 편집:

```properties
n8n.api-key=여기에_복사한_API_키_붙여넣기
```

또는 환경변수로 설정:

```bash
export N8N_API_KEY=여기에_복사한_API_키_붙여넣기
```

### 4단계: Spring Boot 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 IDE에서 `N8nApplication.java`를 실행합니다.

애플리케이션이 `http://localhost:8080`에서 실행됩니다.

### 5단계: 테스트

#### 방법 1: Health Check (가장 간단)

```bash
curl http://localhost:8080/api/public/health
```

성공 응답:
```json
{
  "success": true,
  "message": "Service is healthy",
  "data": {
    "status": "UP",
    "timestamp": "2024-01-01T12:00:00",
    "service": "n8n-integration-api"
  },
  "timestamp": 1234567890
}
```

#### 방법 2: n8n 워크플로우 조회

```bash
curl -X GET http://localhost:8080/api/n8n/workflows \
  -u admin:admin123
```

#### 방법 3: 간단한 계산 API 호출

```bash
curl -X POST http://localhost:8080/api/public/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "add",
    "num1": 10,
    "num2": 5
  }'
```

## n8n 워크플로우 생성 및 테스트

### 예시 1: Spring Boot API 호출하기

1. n8n에서 새 워크플로우 생성
2. 다음 노드들을 추가:

   **Manual Trigger 노드**
   - Start 버튼으로 수동 실행

   **HTTP Request 노드**
   - Method: POST
   - URL: `http://host.docker.internal:8080/api/public/process`
     (Docker 사용 시, 로컬 설치 시 `http://localhost:8080/api/public/process`)
   - Body Content Type: JSON
   - JSON:
   ```json
   {
     "data": "Hello from n8n!"
   }
   ```

3. "Execute Workflow" 클릭하여 테스트

### 예시 2: Webhook 생성 및 Spring Boot에서 호출

1. n8n에서 새 워크플로우 생성
2. **Webhook 노드** 추가
   - HTTP Method: POST
   - Path: `test-webhook`
   - 노드를 클릭하여 Webhook URL 확인 (예: `http://localhost:5678/webhook/test-webhook`)

3. **Respond to Webhook 노드** 추가
   - Respond With: JSON
   - Response Data:
   ```json
   {
     "success": true,
     "message": "Webhook received!",
     "receivedData": "{{ $json }}"
   }
   ```

4. 워크플로우 활성화 (우측 상단 토글)

5. Spring Boot API에서 호출:
```bash
curl -X POST http://localhost:8080/api/n8n/webhook/simple/test-webhook \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "message": "Hello from Spring Boot!",
    "timestamp": "2024-01-01T12:00:00"
  }'
```

## Postman으로 테스트

1. Postman 실행
2. Import 클릭
3. 프로젝트 루트의 `postman_collection.json` 파일 선택
4. 컬렉션이 로드되면 각 요청 테스트 가능

## 문제 해결

### 포트 충돌
```bash
# 8080 포트가 이미 사용 중이면
server.port=8081  # application.properties에서 변경
```

### n8n 연결 실패
- Docker 사용 시: `n8n.base-url=http://host.docker.internal:5678`
- 로컬 설치 시: `n8n.base-url=http://localhost:5678`

### API 키 오류
- n8n Settings > API에서 키가 활성화되어 있는지 확인
- application.properties의 키가 정확한지 확인

### Webhook 404 오류
- n8n 워크플로우가 활성화되어 있는지 확인
- Webhook 경로가 정확한지 확인
- Webhook 노드가 "Listening" 상태인지 확인

## 다음 단계

- `README.md`: 전체 API 문서 참조
- `n8n-workflow-examples.json`: 더 많은 n8n 워크플로우 예시
- `postman_collection.json`: 전체 API 테스트 컬렉션

## 유용한 엔드포인트

```bash
# 헬스 체크
GET http://localhost:8080/api/public/health

# 모든 워크플로우 조회
GET http://localhost:8080/api/n8n/workflows (인증 필요)

# 데이터 처리
POST http://localhost:8080/api/public/process

# 계산
POST http://localhost:8080/api/public/calculate

# Webhook 트리거
POST http://localhost:8080/api/n8n/webhook/simple/{webhook-path} (인증 필요)
```

즐거운 코딩 되세요!
