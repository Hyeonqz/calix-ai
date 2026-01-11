# Change Log: 크롤링 ETL Phase 1 - Entity 및 Repository 작성

**작성일**: 2026-01-07
**작성자**: Claude Code
**작업 유형**: 도메인 모델 설계 및 구현

---

## As-Is (변경 전)

### invest-domain 모듈 상태
- JPA, MySQL, QueryDSL, Lombok 의존성만 설정된 상태
- Entity 클래스 미존재
- Repository 인터페이스 미존재
- 데이터 모델 정의 없음

### 문제점
1. 크롤링 데이터를 저장할 도메인 모델 부재
2. 하루 1회 크롤링 작업 이력을 추적할 방법 없음
3. ETL 파이프라인의 데이터 계층 미구축
4. 데이터 접근 계층(Repository) 없음

---

## To-Be (변경 후)

### 프로젝트 구조
```
invest-domain/src/main/java/io/github/Hyeonqz/
├── domain/
│   ├── common/
│   │   └── BaseTimeEntity.java          (공통 감사 Entity)
│   └── crawling/
│       ├── entity/
│       │   ├── RawCrawledData.java      (원본 크롤링 데이터)
│       │   └── CrawlingJob.java         (작업 이력)
│       ├── repository/
│       │   ├── RawCrawledDataRepository.java
│       │   └── CrawlingJobRepository.java
│       ├── enums/
│       │   ├── CrawlingStatus.java      (PENDING, PROCESSING, PROCESSED, FAILED)
│       │   └── JobStatus.java           (RUNNING, SUCCESS, FAILED)
│       └── config/
│           └── JpaAuditingConfig.java   (JPA Auditing 설정)
```

---

## 주요 변경사항

### 1. Enum 클래스

#### CrawlingStatus.java
**위치**: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/enums/CrawlingStatus.java`

**역할**: 크롤링 데이터의 처리 상태 관리

```java
public enum CrawlingStatus {
    PENDING("대기 중"),       // 크롤링 완료, ETL 처리 대기
    PROCESSING("처리 중"),    // ETL 처리 진행 중
    PROCESSED("처리 완료"),   // ETL 처리 완료
    FAILED("실패");          // 처리 실패
}
```

#### JobStatus.java
**위치**: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/enums/JobStatus.java`

**역할**: 크롤링 작업의 실행 상태 관리

```java
public enum JobStatus {
    RUNNING("실행 중"),   // 작업 실행 중
    SUCCESS("성공"),     // 작업 성공
    FAILED("실패");      // 작업 실패
}
```

---

### 2. BaseTimeEntity (공통 감사 필드)

**위치**: `invest-domain/src/main/java/io/github/Hyeonqz/domain/common/BaseTimeEntity.java`

**역할**: 모든 Entity의 생성일시/수정일시 자동 관리

**주요 코드**:
```java
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

**설계 포인트**:
- `@MappedSuperclass`: 상속받는 Entity에 필드 자동 추가
- `@EntityListeners(AuditingEntityListener.class)`: JPA Auditing 활성화
- `@CreatedDate`, `@LastModifiedDate`: 자동 타임스탬프 관리

---

### 3. RawCrawledData Entity

**위치**: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/entity/RawCrawledData.java`

**역할**: 크롤링한 원본 데이터 저장 (ETL의 Extract 단계)

**테이블 스키마**:
```sql
CREATE TABLE raw_crawled_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_url VARCHAR(2048) NOT NULL,          -- 크롤링 소스 URL
    title VARCHAR(500),                         -- 페이지 제목
    content LONGTEXT NOT NULL,                  -- 원본 HTML/JSON (최대 4GB)
    content_type VARCHAR(50),                   -- HTML, JSON, XML 등
    status VARCHAR(20) NOT NULL,                -- PENDING, PROCESSING, PROCESSED, FAILED
    crawled_at DATETIME NOT NULL,               -- 크롤링 일시
    processed_at DATETIME,                      -- 처리 완료 일시
    error_message VARCHAR(2000),                -- 실패 시 오류 메시지
    created_at DATETIME NOT NULL,               -- 생성일시 (자동)
    updated_at DATETIME NOT NULL,               -- 수정일시 (자동)
    INDEX idx_source_url (source_url(255)),
    INDEX idx_status (status),
    INDEX idx_crawled_at (crawled_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**인덱스 전략**:
1. `idx_source_url`: URL 기반 중복 체크 (최대 255자만 인덱싱)
2. `idx_status`: 상태별 조회 최적화 (PENDING 데이터 조회 등)
3. `idx_crawled_at`: 날짜별 조회 최적화

**비즈니스 로직 메서드**:
```java
public void markAsProcessed() {
    this.status = CrawlingStatus.PROCESSED;
    this.processedAt = LocalDateTime.now();
}

public void markAsFailed(String errorMessage) {
    this.status = CrawlingStatus.FAILED;
    this.errorMessage = errorMessage;
}

public void markAsProcessing() {
    this.status = CrawlingStatus.PROCESSING;
}

public boolean isPending() {
    return this.status == CrawlingStatus.PENDING;
}

public boolean isProcessed() {
    return this.status == CrawlingStatus.PROCESSED;
}
```

**설계 특징**:
- `content` 컬럼: LONGTEXT (최대 4GB) - 큰 HTML 페이지 대응
- `@NoArgsConstructor(access = PROTECTED)`: JPA용 기본 생성자, 외부 직접 생성 방지
- `@Builder`: 명확한 객체 생성 의도 표현
- 상태 변경 로직을 Entity 내부에 캡슐화 (도메인 주도 설계)

---

### 4. CrawlingJob Entity

**위치**: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/entity/CrawlingJob.java`

**역할**: 크롤링 작업의 실행 이력 추적 (하루 1회 배치 작업 모니터링)

**테이블 스키마**:
```sql
CREATE TABLE crawling_job (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,         -- 작업 이름 (예: "DAILY_NEWS_CRAWLING")
    target_url VARCHAR(2048),               -- 크롤링 대상 URL
    status VARCHAR(20) NOT NULL,            -- RUNNING, SUCCESS, FAILED
    started_at DATETIME NOT NULL,           -- 작업 시작 일시
    completed_at DATETIME,                  -- 작업 완료 일시
    total_count INT,                        -- 전체 처리 건수
    success_count INT,                      -- 성공 건수
    fail_count INT,                         -- 실패 건수
    error_message VARCHAR(2000),            -- 실패 시 오류 메시지
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_job_name (job_name),
    INDEX idx_started_at (started_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**비즈니스 로직 메서드**:
```java
public void complete(int successCount, int failCount) {
    this.status = JobStatus.SUCCESS;
    this.completedAt = LocalDateTime.now();
    this.successCount = successCount;
    this.failCount = failCount;
    this.totalCount = successCount + failCount;
}

public void fail(String errorMessage) {
    this.status = JobStatus.FAILED;
    this.completedAt = LocalDateTime.now();
    this.errorMessage = errorMessage;
}

public Long getExecutionTimeInSeconds() {
    if (completedAt == null) return null;
    return Duration.between(startedAt, completedAt).getSeconds();
}
```

**설계 특징**:
- 작업 실행 시간 계산 기능 (`getExecutionTimeInSeconds()`)
- 성공률 추적 (successCount / totalCount)
- 중복 실행 방지를 위한 상태 체크

---

### 5. Repository 인터페이스

#### RawCrawledDataRepository
**위치**: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/repository/RawCrawledDataRepository.java`

**주요 쿼리 메서드**:
```java
// URL로 조회 (중복 체크)
Optional<RawCrawledData> findBySourceUrl(String sourceUrl);

// 상태별 조회
List<RawCrawledData> findByStatus(CrawlingStatus status);

// 특정 기간 내 데이터 조회
@Query("SELECT r FROM RawCrawledData r WHERE r.crawledAt BETWEEN :startDate AND :endDate ORDER BY r.crawledAt DESC")
List<RawCrawledData> findByCrawledAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

// 오래된 PENDING 데이터 조회 (재처리용)
@Query("SELECT r FROM RawCrawledData r WHERE r.status = :status AND r.crawledAt < :threshold ORDER BY r.crawledAt ASC")
List<RawCrawledData> findOldPendingData(@Param("status") CrawlingStatus status, @Param("threshold") LocalDateTime threshold);

// URL 존재 여부 확인
boolean existsBySourceUrl(String sourceUrl);

// 오늘 크롤링된 데이터 조회
List<RawCrawledData> findTodayCrawledData(LocalDateTime startOfDay, LocalDateTime endOfDay);
```

#### CrawlingJobRepository
**위치**: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/repository/CrawlingJobRepository.java`

**주요 쿼리 메서드**:
```java
// 가장 최근 작업 조회
Optional<CrawlingJob> findTopByOrderByStartedAtDesc();

// 상태별 작업 목록
List<CrawlingJob> findByStatus(JobStatus status);

// 실행 중인 작업 존재 여부 (중복 실행 방지)
@Query("SELECT COUNT(j) > 0 FROM CrawlingJob j WHERE j.status = 'RUNNING'")
boolean existsRunningJob();

// 특정 작업명으로 최근 작업 조회
Optional<CrawlingJob> findTopByJobNameOrderByStartedAtDesc(String jobName);

// 평균 성공률 조회
@Query("SELECT AVG(CAST(j.successCount AS double) / NULLIF(j.totalCount, 0)) FROM CrawlingJob j WHERE j.status = 'SUCCESS' AND j.totalCount > 0")
Optional<Double> getAverageSuccessRate();
```

**설계 특징**:
- Spring Data JPA의 메서드 네이밍 컨벤션 활용
- 복잡한 쿼리는 `@Query` 어노테이션 사용
- 통계 쿼리 지원 (평균 성공률 등)

---

### 6. JPA Auditing 설정

**위치**: `invest-domain/src/main/java/io/github/Hyeonqz/domain/config/JpaAuditingConfig.java`

```java
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
```

**역할**: BaseTimeEntity의 `@CreatedDate`, `@LastModifiedDate` 자동 처리 활성화

---

## Impact Analysis

### 1. 영향받는 모듈
- **invest-domain**: 신규 Entity 및 Repository 추가 ✓
- **invest-api**: 향후 invest-domain 의존성 사용
- **invest-batch**: 향후 invest-domain 의존성 사용

### 2. 데이터베이스 스키마
- **신규 테이블 2개**:
  - `raw_crawled_data`: 크롤링 원본 데이터
  - `crawling_job`: 작업 이력

**DDL 자동 생성 설정**:
- invest-batch/application.yml: `ddl-auto: update` (개발 환경)
- invest-api/application.yml: `ddl-auto: validate` (운영 환경)

### 3. API 엔드포인트
- **현재 단계에서는 영향 없음** (Phase 4에서 API 구현 예정)

### 4. 호환성 이슈
- **없음**: 기존 코드와 독립적인 신규 도메인 모델

### 5. 빌드 검증
```bash
./gradlew :invest-domain:build

BUILD SUCCESSFUL in 12s
4 actionable tasks: 4 executed
```

**QueryDSL Q-Class 생성 확인**:
```
build/generated/querydsl/io/github/Hyeonqz/domain/
├── common/QBaseTimeEntity.java
└── crawling/entity/
    ├── QCrawlingJob.java
    └── QRawCrawledData.java
```

---

## Testing

### 빌드 테스트 결과
```bash
# invest-domain 모듈 빌드
./gradlew :invest-domain:build
✓ BUILD SUCCESSFUL in 12s

# 전체 프로젝트 빌드
./gradlew clean build
✓ BUILD SUCCESSFUL in 23s
```

**검증 항목**:
- [x] Entity 클래스 컴파일 성공
- [x] QueryDSL Q-Class 생성 확인
- [x] Repository 인터페이스 컴파일 성공
- [x] JPA Auditing 설정 로드 확인
- [x] 전체 모듈 빌드 성공

### 향후 테스트 계획 (Phase 2 이후)

#### 단위 테스트
```java
@Test
void testRawCrawledData_markAsProcessed() {
    RawCrawledData data = RawCrawledData.builder()
        .sourceUrl("https://example.com")
        .content("test content")
        .status(CrawlingStatus.PENDING)
        .crawledAt(LocalDateTime.now())
        .build();

    data.markAsProcessed();

    assertThat(data.getStatus()).isEqualTo(CrawlingStatus.PROCESSED);
    assertThat(data.getProcessedAt()).isNotNull();
}
```

#### Repository 통합 테스트
```java
@DataJpaTest
class RawCrawledDataRepositoryTest {

    @Autowired
    private RawCrawledDataRepository repository;

    @Test
    void testFindBySourceUrl() {
        String url = "https://test.com";
        RawCrawledData saved = repository.save(
            RawCrawledData.builder()
                .sourceUrl(url)
                .content("content")
                .status(CrawlingStatus.PENDING)
                .crawledAt(LocalDateTime.now())
                .build()
        );

        Optional<RawCrawledData> found = repository.findBySourceUrl(url);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }
}
```

---

## 발생한 이슈 및 해결

### 이슈 없음
- 모든 빌드 및 컴파일이 정상적으로 완료됨
- QueryDSL Q-Class 생성 정상 작동

---

## 롤백 계획

### 롤백 절차
1. Git을 통한 변경사항 복구:
   ```bash
   git checkout HEAD -- invest-domain/src/main/java
   ```
2. 빌드 캐시 삭제:
   ```bash
   ./gradlew :invest-domain:clean
   ```

### 롤백 리스크
- **낮음**: 신규 파일 추가만 진행, 기존 코드 수정 없음
- 데이터베이스 테이블 생성 전이므로 스키마 롤백 불필요

---

## 향후 작업 (Phase 2~5)

### Phase 2: 크롤링 서비스 구현 (invest-external)
- [ ] Jsoup 의존성 추가
- [ ] CrawlingService 구현 (HTML 파싱)
- [ ] WebClient 기반 비동기 HTTP 요청
- [ ] RawCrawledData 생성 로직

### Phase 3: 배치 작업 구현 (invest-batch)
- [ ] Spring Batch Job 구성
- [ ] `@Scheduled`로 하루 1회 실행 설정
- [ ] ETL 파이프라인 구축 (Extract → Transform → Load)
- [ ] CrawlingJob 이력 기록

### Phase 4: REST API 구현 (invest-api)
- [ ] 크롤링 데이터 조회 API
- [ ] 작업 이력 조회 API
- [ ] 통계 API (성공률, 처리 건수 등)

### Phase 5: AI 요약 기능 추가
- [ ] SummaryData Entity 추가
- [ ] AI 요약 서비스 연동 (OpenAI/Claude API)
- [ ] 요약 결과 저장 및 API 제공

---

## 체크리스트

### 구현 완료
- [x] 패키지 구조 생성
- [x] Enum 클래스 작성 (CrawlingStatus, JobStatus)
- [x] BaseTimeEntity 작성
- [x] RawCrawledData Entity 작성
- [x] CrawlingJob Entity 작성
- [x] RawCrawledDataRepository 작성
- [x] CrawlingJobRepository 작성
- [x] JPA Auditing 설정
- [x] invest-domain 모듈 빌드 테스트
- [x] QueryDSL Q-Class 생성 확인
- [x] 전체 프로젝트 빌드 테스트
- [x] 변경사항 문서화

### 미완료 (향후 작업)
- [ ] 단위 테스트 작성
- [ ] Repository 통합 테스트 작성
- [ ] 데이터베이스 연동 테스트
- [ ] 성능 테스트 (대량 INSERT, 인덱스 효과)

---

## 참고 사항

### 금융 도메인 특성 고려
- **트랜잭션 무결성**: 상태 변경 로직을 Entity 내부에 캡슐화
- **감사(Auditing)**: 모든 데이터 변경 이력 자동 기록 (created_at, updated_at)
- **민감정보 마스킹**: 향후 로깅 시 content 필드 마스킹 필요

### 인덱스 전략
- `source_url`: VARCHAR(2048) 컬럼은 전체 인덱싱 불가능하므로 앞 255자만 인덱싱
- 향후 URL 해시값 컬럼 추가 고려 (성능 최적화)

### Lombok 활용
- `@NoArgsConstructor(access = PROTECTED)`: JPA 요구사항 + 외부 생성 방지
- `@AllArgsConstructor`: Builder와 함께 사용
- `@Builder`: 명확한 객체 생성 의도

---

**문서 작성 완료**: 2026-01-07
**최종 빌드 상태**: SUCCESS
**다음 단계**: Phase 2 - 크롤링 서비스 구현 (invest-external)
