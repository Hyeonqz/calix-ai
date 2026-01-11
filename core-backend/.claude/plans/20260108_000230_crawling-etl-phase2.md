# 크롤링 ETL 시스템 Phase 2: 크롤링 서비스 구현

**작성일**: 2026-01-08 00:02
**작업자**: Claude Code
**검토 상태**: 승인 대기 중

---

## 1. 작업 목표 및 범위

### 목표
- invest-external 모듈에 크롤링 서비스 구현
- 3개의 금융 지표 사이트 크롤링 기능 개발
- 크롤링한 데이터를 RawCrawledData Entity로 변환

### 크롤링 대상 사이트
1. **CNN Fear and Greed Index**
   - URL: https://edition.cnn.com/markets/fear-and-greed
   - 데이터: 시장 심리 지수 (0-100)
   - 유형: 정적 HTML

2. **Coinank MVRV Z-Score**
   - URL: https://coinank.com/ko/chart/indicator/mvrv-z-score
   - 데이터: 비트코인 MVRV Z-Score 지표
   - 유형: 동적 차트 (API 또는 JavaScript 렌더링)

3. **CoinMarketCap Fear and Greed Index**
   - URL: https://coinmarketcap.com/ko/charts/fear-and-greed-index/
   - 데이터: 암호화폐 시장 심리 지수
   - 유형: 동적 차트 (API 가능성)

### 범위
**작업 대상**:
- `invest-external` 모듈 (Kotlin)
  - 크롤링 서비스 클래스
  - HTML 파서 클래스
  - DTO 클래스
  - 크롤링 전략 인터페이스

**제외 범위** (향후 Phase에서 진행):
- 배치 작업 스케줄링 (invest-batch - Phase 3)
- REST API 제공 (invest-api - Phase 4)
- AI 요약 기능 (Phase 5)

---

## 2. 현재 상태 분석

### invest-external 모듈 현황
```
invest-external/
├── build.gradle.kts (WebFlux 설정 완료)
├── src/
│   ├── main/
│   │   ├── kotlin/        ← 여기에 크롤링 서비스 작성 예정
│   │   └── resources/
│   └── test/
│       └── kotlin/
```

**현재 설정**:
- Spring WebFlux: ✓ 설정 완료 (WebClient 사용 가능)
- 비동기 HTTP 클라이언트: ✓ 준비 완료

**필요한 추가 의존성**:
- Jsoup: HTML 파싱 라이브러리
- Kotlin Coroutines: 비동기 처리

---

## 3. 기술 스택 선정

### 크롤링 전략

| 사이트 | 크롤링 방법 | 사유 |
|--------|------------|------|
| CNN Fear and Greed | Jsoup (정적 파싱) | 정적 HTML 페이지 |
| Coinank MVRV Z-Score | WebClient + Jsoup | API 엔드포인트 또는 SSR 페이지 |
| CoinMarketCap | WebClient + Jsoup | API 엔드포인트 조사 필요 |

### 선택한 기술
1. **Jsoup 1.17.2**: HTML 파싱
   - 장점: 가볍고 빠름, CSS Selector 지원
   - 단점: JavaScript 렌더링 미지원

2. **WebClient (Spring WebFlux)**: HTTP 요청
   - 장점: 비동기 처리, Reactive Streams 지원
   - 단점: 동적 렌더링 페이지 처리 제한

3. **Kotlin Coroutines**: 비동기 처리
   - 장점: 코드 가독성 높음, suspend 함수 활용

### 동적 페이지 대응 전략
- **1단계**: API 엔드포인트 확인 (개발자 도구 Network 탭)
- **2단계**: API가 없으면 HTML에서 JSON 데이터 추출
- **3단계**: 불가능하면 Selenium/Playwright 고려 (Phase 2.5)

---

## 4. 영향받는 컴포넌트

### 4.1 새로 생성될 파일

#### DTO 클래스 (Kotlin)
1. **CrawlingRequest.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/dto/CrawlingRequest.kt`
   - 역할: 크롤링 요청 데이터

2. **CrawlingResult.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/dto/CrawlingResult.kt`
   - 역할: 크롤링 결과 데이터

3. **FearAndGreedData.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/dto/FearAndGreedData.kt`
   - 역할: Fear and Greed Index 데이터 구조

#### 파서 인터페이스 (Kotlin)
4. **HtmlParser.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/parser/HtmlParser.kt`
   - 역할: HTML 파싱 전략 인터페이스

5. **CnnFearAndGreedParser.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/parser/CnnFearAndGreedParser.kt`
   - 역할: CNN 페이지 파싱

6. **CoinankMvrvParser.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/parser/CoinankMvrvParser.kt`
   - 역할: Coinank 페이지 파싱

7. **CoinMarketCapParser.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/parser/CoinMarketCapParser.kt`
   - 역할: CoinMarketCap 페이지 파싱

#### 서비스 클래스 (Kotlin)
8. **WebCrawlingService.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/service/WebCrawlingService.kt`
   - 역할: 웹 페이지 크롤링 오케스트레이션

9. **HtmlFetchService.kt**
   - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/service/HtmlFetchService.kt`
   - 역할: WebClient를 이용한 HTML 페치

#### 설정 클래스 (Kotlin)
10. **WebClientConfig.kt**
    - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/config/WebClientConfig.kt`
    - 역할: WebClient 빈 설정

#### Enum 클래스 (Kotlin)
11. **CrawlingTarget.kt**
    - 경로: `invest-external/src/main/kotlin/io/github/Hyeonqz/external/crawling/enums/CrawlingTarget.kt`
    - 역할: 크롤링 대상 사이트 정의

---

## 5. 구현 단계별 상세 계획

### Step 1: 의존성 추가

**invest-external/build.gradle.kts**:
```kotlin
dependencies {
    // 기존 의존성
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(project(":module-shared"))

    // 추가 의존성
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
}
```

---

### Step 2: Enum 및 DTO 클래스 작성

#### CrawlingTarget.kt
```kotlin
enum class CrawlingTarget(
    val displayName: String,
    val url: String,
    val contentType: String
) {
    CNN_FEAR_AND_GREED(
        displayName = "CNN Fear and Greed Index",
        url = "https://edition.cnn.com/markets/fear-and-greed",
        contentType = "HTML"
    ),
    COINANK_MVRV(
        displayName = "Coinank MVRV Z-Score",
        url = "https://coinank.com/ko/chart/indicator/mvrv-z-score",
        contentType = "HTML"
    ),
    COINMARKETCAP_FEAR_GREED(
        displayName = "CoinMarketCap Fear and Greed Index",
        url = "https://coinmarketcap.com/ko/charts/fear-and-greed-index/",
        contentType = "HTML"
    );
}
```

#### CrawlingRequest.kt
```kotlin
data class CrawlingRequest(
    val target: CrawlingTarget,
    val url: String = target.url,
    val timeout: Long = 30000L // 30초
)
```

#### CrawlingResult.kt
```kotlin
data class CrawlingResult(
    val target: CrawlingTarget,
    val sourceUrl: String,
    val title: String,
    val rawHtml: String,
    val parsedData: Map<String, Any>,
    val crawledAt: LocalDateTime = LocalDateTime.now(),
    val success: Boolean = true,
    val errorMessage: String? = null
)
```

#### FearAndGreedData.kt
```kotlin
data class FearAndGreedData(
    val value: Int,              // 0-100
    val classification: String,   // "Extreme Fear", "Fear", "Neutral", "Greed", "Extreme Greed"
    val timestamp: LocalDateTime
)
```

---

### Step 3: WebClient 설정

#### WebClientConfig.kt
```kotlin
@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .defaultHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9")
            .codecs { it.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) } // 10MB
            .build()
    }
}
```

---

### Step 4: HTML 페치 서비스

#### HtmlFetchService.kt
```kotlin
@Service
class HtmlFetchService(
    private val webClient: WebClient
) {
    private val logger = KotlinLogging.logger {}

    suspend fun fetchHtml(url: String, timeout: Long = 30000L): String {
        logger.info { "Fetching HTML from: $url" }

        return try {
            webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String::class.java)
                .timeout(Duration.ofMillis(timeout))
                .awaitSingle()
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch HTML from: $url" }
            throw CrawlingException("HTML 페치 실패: ${e.message}", e)
        }
    }
}
```

---

### Step 5: HTML 파서 인터페이스 및 구현체

#### HtmlParser.kt (인터페이스)
```kotlin
interface HtmlParser<T> {
    fun parse(html: String): T
    fun supports(target: CrawlingTarget): Boolean
}
```

#### CnnFearAndGreedParser.kt
```kotlin
@Component
class CnnFearAndGreedParser : HtmlParser<FearAndGreedData> {
    private val logger = KotlinLogging.logger {}

    override fun parse(html: String): FearAndGreedData {
        val doc = Jsoup.parse(html)

        // CSS Selector로 데이터 추출
        val valueElement = doc.selectFirst("div.fear-and-greed-gauge__value")
        val classificationElement = doc.selectFirst("div.fear-and-greed-gauge__classification")

        val value = valueElement?.text()?.toIntOrNull()
            ?: throw CrawlingException("Fear and Greed 값을 찾을 수 없습니다")

        val classification = classificationElement?.text()
            ?: "Unknown"

        logger.info { "Parsed CNN Fear and Greed: value=$value, classification=$classification" }

        return FearAndGreedData(
            value = value,
            classification = classification,
            timestamp = LocalDateTime.now()
        )
    }

    override fun supports(target: CrawlingTarget): Boolean {
        return target == CrawlingTarget.CNN_FEAR_AND_GREED
    }
}
```

#### CoinankMvrvParser.kt
```kotlin
@Component
class CoinankMvrvParser : HtmlParser<Map<String, Any>> {
    private val logger = KotlinLogging.logger {}

    override fun parse(html: String): Map<String, Any> {
        val doc = Jsoup.parse(html)

        // Option 1: JSON 데이터가 script 태그에 있는 경우
        val scriptElements = doc.select("script")
        for (script in scriptElements) {
            val scriptContent = script.html()
            if (scriptContent.contains("mvrvData") || scriptContent.contains("chartData")) {
                // JSON 파싱 로직
                logger.info { "Found MVRV data in script tag" }
                // TODO: JSON 추출 및 파싱
            }
        }

        // Option 2: API 엔드포인트 호출 (추후 구현)

        // 임시 응답
        return mapOf(
            "source" to "Coinank",
            "indicator" to "MVRV Z-Score",
            "note" to "동적 데이터 파싱 필요"
        )
    }

    override fun supports(target: CrawlingTarget): Boolean {
        return target == CrawlingTarget.COINANK_MVRV
    }
}
```

#### CoinMarketCapParser.kt
```kotlin
@Component
class CoinMarketCapParser : HtmlParser<Map<String, Any>> {
    private val logger = KotlinLogging.logger {}

    override fun parse(html: String): Map<String, Any> {
        val doc = Jsoup.parse(html)

        // CoinMarketCap은 API를 사용할 가능성이 높음
        // 개발자 도구로 Network 탭 확인 필요

        logger.info { "Parsing CoinMarketCap Fear and Greed Index" }

        // 임시 응답
        return mapOf(
            "source" to "CoinMarketCap",
            "indicator" to "Fear and Greed Index",
            "note" to "API 엔드포인트 확인 필요"
        )
    }

    override fun supports(target: CrawlingTarget): Boolean {
        return target == CrawlingTarget.COINMARKETCAP_FEAR_GREED
    }
}
```

---

### Step 6: 크롤링 오케스트레이션 서비스

#### WebCrawlingService.kt
```kotlin
@Service
class WebCrawlingService(
    private val htmlFetchService: HtmlFetchService,
    private val parsers: List<HtmlParser<*>>
) {
    private val logger = KotlinLogging.logger {}

    suspend fun crawl(request: CrawlingRequest): CrawlingResult {
        logger.info { "Starting crawling: ${request.target.displayName}" }

        return try {
            // 1. HTML 페치
            val html = htmlFetchService.fetchHtml(request.url, request.timeout)

            // 2. 파서 선택
            val parser = parsers.firstOrNull { it.supports(request.target) }
                ?: throw CrawlingException("Parser not found for target: ${request.target}")

            // 3. HTML 파싱
            val parsedData = parser.parse(html)

            // 4. 결과 반환
            CrawlingResult(
                target = request.target,
                sourceUrl = request.url,
                title = extractTitle(html),
                rawHtml = html,
                parsedData = convertToMap(parsedData),
                success = true
            )
        } catch (e: Exception) {
            logger.error(e) { "Crawling failed: ${request.target.displayName}" }
            CrawlingResult(
                target = request.target,
                sourceUrl = request.url,
                title = "",
                rawHtml = "",
                parsedData = emptyMap(),
                success = false,
                errorMessage = e.message
            )
        }
    }

    suspend fun crawlAll(): List<CrawlingResult> {
        return CrawlingTarget.values().map { target ->
            val request = CrawlingRequest(target)
            crawl(request)
        }
    }

    private fun extractTitle(html: String): String {
        val doc = Jsoup.parse(html)
        return doc.title()
    }

    private fun convertToMap(data: Any): Map<String, Any> {
        return when (data) {
            is Map<*, *> -> data as Map<String, Any>
            is FearAndGreedData -> mapOf(
                "value" to data.value,
                "classification" to data.classification,
                "timestamp" to data.timestamp.toString()
            )
            else -> mapOf("data" to data.toString())
        }
    }
}
```

---

### Step 7: 예외 처리

#### CrawlingException.kt
```kotlin
class CrawlingException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
```

---

## 6. 예상 리스크 및 대응 방안

### 리스크 1: 동적 렌더링 페이지 (JavaScript)
**문제**: Coinank, CoinMarketCap은 JavaScript로 데이터를 렌더링할 가능성 높음

**대응**:
1. **1차 대응**: 개발자 도구로 API 엔드포인트 확인
   - Network 탭에서 XHR/Fetch 요청 분석
   - API 엔드포인트 직접 호출

2. **2차 대응**: HTML 내 embedded JSON 추출
   - `<script>` 태그 내 `window.__INITIAL_STATE__` 등의 JSON 데이터 파싱

3. **3차 대응**: Selenium/Playwright 도입 (Phase 2.5)
   - 헤드리스 브라우저로 JavaScript 실행 후 HTML 추출

---

### 리스크 2: 크롤링 차단 (Rate Limiting, IP 차단)
**문제**: 빈번한 크롤링 시 사이트에서 차단 가능

**대응**:
- User-Agent 설정 (이미 WebClientConfig에 포함)
- 요청 간 딜레이 추가 (하루 1회 크롤링이므로 문제 없음)
- Proxy/VPN 고려 (필요시)

---

### 리스크 3: HTML 구조 변경
**문제**: 웹사이트 구조 변경 시 파서 실패

**대응**:
- 파싱 실패 시 에러 로그 기록
- 알림 시스템 구축 (Phase 3)
- CSS Selector를 유연하게 작성
- 정기적인 파서 유효성 검증

---

### 리스크 4: CORS 이슈
**문제**: 브라우저 기반 크롤링 시 CORS 에러 발생 가능

**대응**:
- 서버 사이드 크롤링(WebClient)이므로 CORS 문제 없음
- 만약 API 호출 시 CORS 발생하면 프록시 서버 구축

---

### 리스크 5: 타임아웃
**문제**: 느린 네트워크 또는 서버 응답 지연

**대응**:
- 타임아웃 설정: 30초 (조정 가능)
- 재시도 로직 구현 (Phase 3)
- 비동기 처리로 블로킹 방지

---

## 7. 테스트 전략

### 7.1 단위 테스트

#### 파서 테스트
```kotlin
@Test
fun `CNN Fear and Greed Parser should extract value`() {
    val html = """
        <div class="fear-and-greed-gauge__value">65</div>
        <div class="fear-and-greed-gauge__classification">Greed</div>
    """.trimIndent()

    val parser = CnnFearAndGreedParser()
    val result = parser.parse(html)

    assertThat(result.value).isEqualTo(65)
    assertThat(result.classification).isEqualTo("Greed")
}
```

### 7.2 통합 테스트

#### 실제 크롤링 테스트 (수동)
```kotlin
@SpringBootTest
class WebCrawlingServiceIntegrationTest {

    @Autowired
    lateinit var webCrawlingService: WebCrawlingService

    @Test
    fun `Crawl CNN Fear and Greed Index`() = runBlocking {
        val request = CrawlingRequest(CrawlingTarget.CNN_FEAR_AND_GREED)
        val result = webCrawlingService.crawl(request)

        assertThat(result.success).isTrue()
        assertThat(result.rawHtml).isNotEmpty()
        assertThat(result.parsedData).containsKey("value")
    }
}
```

### 7.3 빌드 테스트
```bash
./gradlew :invest-external:build
```

---

## 8. 작업 체크리스트

### Pre-work
- [x] Phase 1 완료 확인
- [ ] 사용자 승인 획득
- [ ] 크롤링 대상 사이트 접근 가능 여부 확인

### Implementation
- [ ] Step 1: 의존성 추가 (Jsoup, Coroutines)
- [ ] Step 2: Enum 및 DTO 클래스 작성
- [ ] Step 3: WebClient 설정
- [ ] Step 4: HtmlFetchService 작성
- [ ] Step 5: HtmlParser 인터페이스 및 구현체 작성
- [ ] Step 6: WebCrawlingService 작성
- [ ] Step 7: 예외 처리 클래스 작성

### Verification
- [ ] 빌드 테스트 (`./gradlew :invest-external:build`)
- [ ] CNN Fear and Greed 크롤링 테스트
- [ ] Coinank MVRV 크롤링 테스트
- [ ] CoinMarketCap 크롤링 테스트
- [ ] 에러 핸들링 테스트

### Documentation
- [ ] `.claude/docs/changes/20260108_crawling-etl-phase2.md` 작성
- [ ] API 엔드포인트 조사 결과 문서화
- [ ] 크롤링 전략 문서화

---

## 9. 다음 단계 (Phase 3)

### Phase 3: 배치 작업 구현 (invest-batch)
- [ ] Spring Batch Job 구성
- [ ] 하루 1회 스케줄링 (`@Scheduled`)
- [ ] WebCrawlingService 호출
- [ ] RawCrawledData 저장
- [ ] CrawlingJob 이력 기록

---

## 10. 중요 참고사항

### 웹 크롤링 법적 이슈
- **robots.txt 확인**: 각 사이트의 크롤링 정책 준수
- **이용 약관 확인**: 상업적 이용 가능 여부
- **데이터 사용 범위**: 개인 학습/연구 목적으로 제한

### 크롤링 에티켓
- 서버 부하 최소화 (하루 1회 크롤링)
- User-Agent 명시
- Rate Limiting 준수

---

## 11. 승인 요청

본 계획을 검토하시고 다음 사항을 확인해주세요:

1. **크롤링 전략**: 3개 사이트에 대한 접근 방법이 적절한가요?
2. **동적 페이지 대응**: API 엔드포인트 조사가 필요한가요?
3. **파서 구조**: CSS Selector 기반 파싱이 충분한가요?
4. **예외 처리**: 추가로 고려할 예외 상황이 있나요?

승인하시면 즉시 Step 1부터 순차적으로 구현을 시작하겠습니다.

**특별 요청**: 구현 전에 3개 사이트의 HTML 구조를 먼저 분석하여 정확한 CSS Selector를 파악하는 것이 좋습니다. 제가 먼저 사이트 구조를 조사하고 파서를 작성할까요?

---

**계획 작성 완료일**: 2026-01-08 00:02
**다음 단계**: 사용자 승인 대기 → Phase 2 Implementation 진행
