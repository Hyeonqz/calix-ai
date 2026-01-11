# Calix AI Engine

FastAPI 기반의 AI 엔진 마이크로서비스로, 금융 데이터 분석 및 예측 모델을 제공합니다.
Java/Spring Boot 메인 서버의 요청을 받아 AI 처리를 수행하는 stateless API 서비스입니다.

## 목차

- [아키텍처 개요](#아키텍처-개요)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [새 기능 추가 방법](#새-기능-추가-방법)
- [Spring Boot 연동](#spring-boot-연동)
- [환경 설정](#환경-설정)
- [테스트](#테스트)
- [API 문서](#api-문서)

## 아키텍처 개요

### 설계 원칙

- **Stateless Design**: 데이터베이스 연결 없이 순수한 연산/처리만 수행
- **File-Type Architecture**: 계층형 구조로 Java/Spring Boot 개발자에게 친숙한 패턴
- **Type-Safe**: 모든 요청/응답은 Pydantic 모델로 타입 검증
- **Async-First**: I/O 작업은 비동기 처리로 성능 최적화

### 기술 스택

- **Python**: 3.11+
- **Framework**: FastAPI
- **Server**: Uvicorn (ASGI)
- **Validation**: Pydantic
- **Testing**: pytest, pytest-asyncio
- **External APIs**: YFinance, OpenAI, LangChain

### 아키텍처 결정 기록 (ADR)

프로젝트의 주요 아키텍처 결정사항은 [.claude/docs/adr/](.claude/docs/adr/)에서 확인할 수 있습니다:

- [ADR-0001: File-Type Architecture](.claude/docs/adr/0001-file-type-architecture.md)
- [ADR-0002: Stateless Design](.claude/docs/adr/0002-stateless-design.md)
- [ADR-0003: Pydantic Settings](.claude/docs/adr/0003-pydantic-settings.md)
- [ADR-0004: API Versioning](.claude/docs/adr/0004-api-versioning.md)

## 프로젝트 구조

```
ai-engine/
├── app/
│   ├── main.py                      # FastAPI 애플리케이션 진입점
│   ├── config/
│   │   └── settings.py              # 환경 설정 (Pydantic Settings)
│   ├── api/
│   │   ├── dependencies.py          # 공통 의존성
│   │   └── v1/                      # API 버전 v1
│   │       ├── router.py            # 라우터 통합
│   │       └── endpoints/
│   │           ├── health.py        # 헬스체크
│   │           └── stocks.py        # 주식 데이터 API
│   ├── schemas/                     # Pydantic 모델 (DTO)
│   │   ├── base.py                  # 공통 응답 모델
│   │   └── stock.py                 # 주식 관련 스키마
│   ├── services/                    # 비즈니스 로직
│   │   └── stock_service.py         # 주식 데이터 처리
│   ├── models/                      # ML 모델 (DB 모델 아님)
│   ├── core/                        # 핵심 유틸리티
│   │   ├── logging.py               # 로깅 설정
│   │   ├── errors.py                # 커스텀 예외
│   │   └── middleware.py            # 미들웨어
│   └── utils/                       # 유틸 함수
├── tests/                           # 테스트
│   ├── conftest.py                  # Pytest fixtures
│   ├── test_api/
│   └── test_services/
├── .claude/docs/
│   ├── adr/                         # Architecture Decision Records
│   └── changes/                     # 변경 이력
├── .env.example                     # 환경변수 템플릿
├── requirements.txt                 # Python 의존성
├── pyproject.toml                   # 프로젝트 메타데이터
└── README.md                        # 이 파일
```

### 계층별 역할

| 계층 | 역할 | Spring Boot 비유 |
|------|------|------------------|
| `api/` | HTTP 요청/응답 처리 | `@RestController` |
| `services/` | 비즈니스 로직 | `@Service` |
| `schemas/` | 요청/응답 데이터 모델 | DTO (Data Transfer Object) |
| `config/` | 설정 관리 | `application.yml` + `@ConfigurationProperties` |
| `core/` | 공통 유틸리티 | Common utilities |

## 시작하기

### 1. 가상환경 생성 및 활성화

```bash
python3 -m venv .venv
source .venv/bin/activate  # Linux/Mac
# .venv\Scripts\activate  # Windows
```

### 2. 의존성 설치

```bash
pip install -r requirements.txt
```

### 3. 환경 설정

`.env.example` 파일을 복사하여 `.env` 파일을 생성하고 필요한 값을 설정합니다:

```bash
cp .env.example .env
```

`.env` 파일 예시:
```bash
APP_NAME=Calix AI Engine
ENVIRONMENT=development
DEBUG=true
OPENAI_API_KEY=your-openai-api-key-here
```

### 4. 서버 실행

```bash
uvicorn app.main:app --reload --port 8000
```

### 5. API 문서 확인

브라우저에서 다음 URL로 접속:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## 새 기능 추가 방법

새로운 기능을 추가할 때는 다음 순서를 따릅니다:

### 예제: Sentiment Analysis 기능 추가

#### Step 1: Schema 정의

`app/schemas/sentiment.py` 파일을 생성하고 요청/응답 모델을 정의합니다:

```python
from pydantic import BaseModel, Field
from typing import Literal

class SentimentRequest(BaseModel):
    """감성 분석 요청 모델"""
    text: str = Field(..., min_length=1, max_length=5000, description="분석할 텍스트")

class SentimentSchema(BaseModel):
    """감성 분석 응답 모델"""
    text: str
    sentiment: Literal["positive", "negative", "neutral"]
    confidence: float = Field(..., ge=0.0, le=1.0)
```

#### Step 2: Service 구현

`app/services/sentiment_service.py` 파일을 생성하고 비즈니스 로직을 구현합니다:

```python
from app.schemas.sentiment import SentimentSchema
from app.core.errors import ModelInferenceError
from app.core.logging import get_logger

logger = get_logger(__name__)

class SentimentService:
    """감성 분석 서비스"""

    async def analyze_sentiment(self, text: str) -> SentimentSchema:
        """
        텍스트의 감성을 분석합니다.

        Args:
            text: 분석할 텍스트

        Returns:
            SentimentSchema: 감성 분석 결과

        Raises:
            ModelInferenceError: 모델 추론 실패 시
        """
        logger.info(f"Analyzing sentiment for text length: {len(text)}")

        try:
            # LangChain 또는 OpenAI API를 사용한 감성 분석 로직
            # ...

            result = SentimentSchema(
                text=text,
                sentiment="positive",
                confidence=0.95
            )

            logger.info(f"Sentiment analysis completed: {result.sentiment}")
            return result

        except Exception as e:
            logger.error(f"Sentiment analysis failed: {str(e)}")
            raise ModelInferenceError(
                message="Failed to analyze sentiment",
                details={"error": str(e)}
            )
```

#### Step 3: Endpoint 생성

`app/api/v1/endpoints/sentiment.py` 파일을 생성하고 API 엔드포인트를 정의합니다:

```python
import logging
from fastapi import APIRouter, Depends

from app.api.dependencies import get_request_logger
from app.schemas.base import DataResponse
from app.schemas.sentiment import SentimentRequest, SentimentSchema
from app.services.sentiment_service import SentimentService

router = APIRouter(prefix="/sentiment", tags=["sentiment"])

@router.post("/analyze", response_model=DataResponse[SentimentSchema])
async def analyze_sentiment(
    request: SentimentRequest,
    logger: logging.Logger = Depends(get_request_logger)
) -> DataResponse[SentimentSchema]:
    """
    텍스트 감성 분석 엔드포인트.

    Spring Boot 서버에서 호출하여 텍스트의 감성을 분석합니다.
    """
    logger.info(f"Received sentiment analysis request")

    service = SentimentService()
    result = await service.analyze_sentiment(request.text)

    return DataResponse[SentimentSchema](
        data=result,
        message="Sentiment analysis completed successfully"
    )
```

#### Step 4: Router 등록

`app/api/v1/router.py` 파일에 새로운 라우터를 추가합니다:

```python
from app.api.v1.endpoints import health, stocks, sentiment

api_router = APIRouter()
api_router.include_router(health.router)
api_router.include_router(stocks.router)
api_router.include_router(sentiment.router)  # 추가
```

#### Step 5: 테스트 작성

`tests/test_api/test_v1/test_sentiment.py` 파일을 생성하고 테스트를 작성합니다:

```python
from fastapi.testclient import TestClient

def test_sentiment_analysis(client: TestClient):
    response = client.post(
        "/api/v1/sentiment/analyze",
        json={"text": "This is a great product!"}
    )

    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert "data" in data
    assert "sentiment" in data["data"]
```

### 이제 사용 가능!

서버를 재시작하면 새로운 엔드포인트가 자동으로 등록됩니다:

```bash
POST /api/v1/sentiment/analyze
```

## Spring Boot 연동

### FastAPI 응답 형식

모든 API는 일관된 응답 형식을 사용합니다:

**성공 응답:**
```json
{
  "success": true,
  "message": "Stock price retrieved successfully for AAPL",
  "data": {
    "ticker": "AAPL",
    "current_price": 182.52,
    "currency": "USD",
    "market_status": "open"
  }
}
```

**에러 응답:**
```json
{
  "success": false,
  "message": "Unable to fetch stock data for INVALID",
  "details": {
    "ticker": "INVALID",
    "error": "Invalid ticker or data not available"
  }
}
```

### Spring Boot 클라이언트 예제

```java
@Service
public class AIEngineClient {

    @Value("${ai.engine.url}")
    private String aiEngineUrl;  // http://ai-engine:8000

    private final RestTemplate restTemplate;

    public StockPriceResponse getStockPrice(String ticker) {
        String url = aiEngineUrl + "/api/v1/stocks/price";
        StockPriceRequest request = new StockPriceRequest(ticker);

        try {
            ResponseEntity<DataResponse> response = restTemplate.postForEntity(
                url,
                request,
                DataResponse.class
            );

            if (response.getBody().isSuccess()) {
                return response.getBody().getData();
            } else {
                throw new AIEngineException(response.getBody().getMessage());
            }
        } catch (RestClientException e) {
            log.error("Failed to call AI Engine: {}", e.getMessage());
            throw new AIEngineException("AI Engine communication failed", e);
        }
    }
}
```

### Spring Boot DTO 예제

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceRequest {
    private String ticker;
}

@Data
public class DataResponse<T> {
    private boolean success;
    private String message;
    private T data;
}

@Data
public class StockPriceData {
    private String ticker;
    private double currentPrice;
    private String currency;
    private String marketStatus;
}
```

## 환경 설정

### 환경변수 목록

| 변수명 | 설명 | 기본값 | 필수 |
|--------|------|--------|------|
| `APP_NAME` | 애플리케이션 이름 | Calix AI Engine | No |
| `APP_VERSION` | 애플리케이션 버전 | 1.0.0 | No |
| `ENVIRONMENT` | 실행 환경 (development/staging/production) | development | No |
| `DEBUG` | 디버그 모드 활성화 | False | No |
| `API_V1_PREFIX` | API v1 경로 prefix | /api/v1 | No |
| `ALLOWED_ORIGINS` | CORS 허용 도메인 (콤마 구분) | http://localhost:8080 | No |
| `OPENAI_API_KEY` | OpenAI API 키 | None | Yes (OpenAI 사용 시) |
| `LANGCHAIN_API_KEY` | LangChain API 키 | None | Yes (LangChain 사용 시) |
| `LOG_LEVEL` | 로그 레벨 (DEBUG/INFO/WARNING/ERROR) | INFO | No |

### 환경별 설정

**개발 환경 (.env.development)**
```bash
ENVIRONMENT=development
DEBUG=true
LOG_LEVEL=DEBUG
ALLOWED_ORIGINS=http://localhost:8080,http://localhost:3000
```

**운영 환경 (.env.production)**
```bash
ENVIRONMENT=production
DEBUG=false
LOG_LEVEL=INFO
ALLOWED_ORIGINS=https://api.calix.com
```

## 테스트

### 테스트 실행

```bash
# 전체 테스트 실행
pytest

# 특정 파일 테스트
pytest tests/test_api/test_v1/test_stocks.py

# verbose 모드
pytest -v

# 커버리지 포함
pytest --cov=app --cov-report=html

# 커버리지 리포트 확인
open htmlcov/index.html
```

### 테스트 작성 가이드

모든 테스트는 `tests/` 디렉토리에 작성하며, 다음 패턴을 따릅니다:

```python
from fastapi.testclient import TestClient

def test_endpoint_name(client: TestClient):
    """테스트 설명"""
    # Given
    request_data = {"key": "value"}

    # When
    response = client.post("/api/v1/endpoint", json=request_data)

    # Then
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
```

## API 문서

### 엔드포인트 목록

#### Health Check

- `GET /health`: 기본 상태 확인
- `GET /api/v1/health`: 헬스체크
- `GET /api/v1/health/ready`: 준비 상태 확인

#### Stock API

- `POST /api/v1/stocks/price`: 주식 현재가 조회

### 자동 생성 문서

FastAPI는 자동으로 API 문서를 생성합니다:

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc
- **OpenAPI Schema**: http://localhost:8000/openapi.json

### 예제 요청

```bash
# Health Check
curl http://localhost:8000/api/v1/health

# Stock Price
curl -X POST http://localhost:8000/api/v1/stocks/price \
  -H "Content-Type: application/json" \
  -d '{"ticker": "AAPL"}'
```

## 개발 가이드

### 코드 스타일

- **Type Hints**: 모든 함수에 타입 힌트 필수
- **Docstrings**: 모든 public 함수에 docstring 작성
- **Async/Await**: I/O 작업은 비동기 처리
- **Error Handling**: 커스텀 예외 사용

### 로깅

```python
from app.core.logging import get_logger

logger = get_logger(__name__)

logger.info("처리 시작")
logger.error(f"에러 발생: {error}")
```

### 에러 처리

```python
from app.core.errors import ExternalAPIError

try:
    result = await external_api_call()
except Exception as e:
    raise ExternalAPIError(
        message="API 호출 실패",
        details={"error": str(e)}
    )
```

## 배포

### Docker (예정)

```bash
docker build -t ai-engine:latest .
docker run -p 8000:8000 --env-file .env ai-engine:latest
```

### 프로덕션 실행

```bash
# Gunicorn with Uvicorn workers
gunicorn app.main:app \
  --workers 4 \
  --worker-class uvicorn.workers.UvicornWorker \
  --bind 0.0.0.0:8000
```

## 문제 해결

### 일반적인 문제

**Q: 서버 시작 시 "ModuleNotFoundError" 발생**
- A: 가상환경이 활성화되어 있는지 확인하고 `pip install -r requirements.txt` 재실행

**Q: "Settings validation error" 발생**
- A: `.env` 파일의 환경변수 설정을 확인하고 필수 값이 모두 설정되었는지 확인

**Q: Stock API 호출 시 400 에러 발생**
- A: 올바른 ticker 심볼을 사용하고 있는지 확인 (예: AAPL, GOOGL)

## 라이센스

Private project

## 기여

프로젝트 기여 가이드라인은 팀 내부 문서를 참조하세요.

## 문의

프로젝트 관련 문의사항은 팀 리더에게 문의하세요.
