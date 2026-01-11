# 크롤링 ETL 시스템 Phase 1: Entity 및 Repository 작성

**작성일**: 2026-01-07 23:47
**작업자**: Claude Code
**검토 상태**: 승인 대기 중

---

## 1. 작업 목표 및 범위

### 목표
- 크롤링 데이터 수집 및 ETL 처리를 위한 Domain 계층 구축
- invest-domain 모듈에 Java 기반 JPA Entity 및 Repository 작성
- 하루 1회 크롤링 작업을 위한 데이터 모델 설계

### 범위
**작업 대상**:
- `invest-domain` 모듈 (Java)
  - Entity 클래스 2개 (RawCrawledData, CrawlingJob)
  - Repository 인터페이스 2개
  - QueryDSL 설정 및 Custom Repository (선택)

**제외 범위** (향후 Phase에서 진행):
- AI 요약 기능 (SummaryData Entity는 나중에 추가)
- 크롤링 서비스 구현 (invest-external)
- 배치 작업 스케줄링 (invest-batch)
- REST API 구현 (invest-api)

---

## 2. 현재 상태 분석

### invest-domain 모듈 현황
```
invest-domain/
├── build.gradle.kts (JPA, MySQL, QueryDSL, Lombok 설정 완료)
├── src/
│   ├── main/
│   │   ├── java/           ← 여기에 Entity 작성 예정
│   │   └── resources/
│   └── test/
│       └── java/
```

**현재 설정**:
- JPA/Hibernate: ✓ 설정 완료
- MySQL Connector: ✓ 설정 완료
- QueryDSL: ✓ 설정 완료 (APT 프로세서)
- Lombok: ✓ 설정 완료

**문제점**:
- Entity 클래스 미존재 → 도메인 모델 정의 필요
- Repository 미존재 → 데이터 접근 계층 구현 필요
- 패키지 구조 미정의

---

## 3. 영향받는 컴포넌트

### 3.1 새로 생성될 파일

#### Entity 클래스 (Java)
1. **RawCrawledData.java**
   - 경로: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/entity/RawCrawledData.java`
   - 역할: 크롤링한 원본 데이터 저장
   - 필드:
     - id (PK)
     - sourceUrl (크롤링 소스 URL)
     - title (페이지 제목)
     - content (원본 HTML/텍스트)
     - contentType (HTML, JSON 등)
     - status (PENDING, PROCESSED, FAILED)
     - crawledAt (크롤링 일시)
     - processedAt (처리 완료 일시)
     - errorMessage (실패 시 오류 메시지)
     - createdAt, updatedAt (감사 필드)

2. **CrawlingJob.java**
   - 경로: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/entity/CrawlingJob.java`
   - 역할: 크롤링 작업 이력 관리
   - 필드:
     - id (PK)
     - jobName (작업 이름)
     - targetUrl (대상 URL)
     - status (RUNNING, SUCCESS, FAILED)
     - startedAt (시작 시각)
     - completedAt (완료 시각)
     - totalCount (전체 크롤링 수)
     - successCount (성공 수)
     - failCount (실패 수)
     - errorMessage

#### Repository 인터페이스 (Java)
3. **RawCrawledDataRepository.java**
   - 경로: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/repository/RawCrawledDataRepository.java`
   - 기본 CRUD + 커스텀 쿼리 메서드

4. **CrawlingJobRepository.java**
   - 경로: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/repository/CrawlingJobRepository.java`
   - 작업 이력 조회

#### Enum 클래스 (Java)
5. **CrawlingStatus.java**
   - 경로: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/enums/CrawlingStatus.java`
   - PENDING, PROCESSING, PROCESSED, FAILED

6. **JobStatus.java**
   - 경로: `invest-domain/src/main/java/io/github/Hyeonqz/domain/crawling/enums/JobStatus.java`
   - RUNNING, SUCCESS, FAILED

#### Base Entity (Java)
7. **BaseTimeEntity.java**
   - 경로: `invest-domain/src/main/java/io/github/Hyeonqz/domain/common/BaseTimeEntity.java`
   - createdAt, updatedAt 공통 필드
   - @MappedSuperclass

---

## 4. 구현 단계별 상세 계획

### Step 1: 패키지 구조 생성
```
invest-domain/src/main/java/io/github/Hyeonqz/
├── domain/
│   ├── common/
│   │   └── BaseTimeEntity.java          (공통 감사 Entity)
│   └── crawling/
│       ├── entity/
│       │   ├── RawCrawledData.java      (원본 데이터)
│       │   └── CrawlingJob.java         (작업 이력)
│       ├── repository/
│       │   ├── RawCrawledDataRepository.java
│       │   └── CrawlingJobRepository.java
│       └── enums/
│           ├── CrawlingStatus.java
│           └── JobStatus.java
```

**패키지 명명 규칙**:
- 현재 프로젝트는 `io.github.Hyeonqz` 사용 중 (CLAUDE.md의 `com.company.investment`와 다름)
- 일관성을 위해 현재 사용 중인 패키지 구조 유지

---

### Step 2: Enum 클래스 작성

#### CrawlingStatus.java
```java
package io.github.Hyeonqz.domain.crawling.enums;

public enum CrawlingStatus {
    PENDING("대기 중"),
    PROCESSING("처리 중"),
    PROCESSED("처리 완료"),
    FAILED("실패");

    private final String description;

    CrawlingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

#### JobStatus.java
```java
package io.github.Hyeonqz.domain.crawling.enums;

public enum JobStatus {
    RUNNING("실행 중"),
    SUCCESS("성공"),
    FAILED("실패");

    private final String description;

    JobStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

---

### Step 3: BaseTimeEntity 작성 (공통 감사 필드)

```java
package io.github.Hyeonqz.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

**JPA Auditing 활성화 필요**:
- `@EnableJpaAuditing` 어노테이션을 Application 클래스에 추가해야 함
- 또는 별도 Configuration 클래스 생성

---

### Step 4: RawCrawledData Entity 작성

```java
package io.github.Hyeonqz.domain.crawling.entity;

import io.github.Hyeonqz.domain.common.BaseTimeEntity;
import io.github.Hyeonqz.domain.crawling.enums.CrawlingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "raw_crawled_data",
    indexes = {
        @Index(name = "idx_source_url", columnList = "source_url"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_crawled_at", columnList = "crawled_at")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RawCrawledData extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_url", nullable = false, length = 2048)
    private String sourceUrl;

    @Column(name = "title", length = 500)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "content_type", length = 50)
    private String contentType; // HTML, JSON, XML 등

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CrawlingStatus status;

    @Column(name = "crawled_at", nullable = false)
    private LocalDateTime crawledAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    // 비즈니스 로직 메서드
    public void markAsProcessed() {
        this.status = CrawlingStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = CrawlingStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    public boolean isPending() {
        return this.status == CrawlingStatus.PENDING;
    }
}
```

**설계 포인트**:
1. **인덱스 전략**:
   - `source_url`: URL 기반 중복 체크
   - `status`: 상태별 조회 (PENDING 데이터 조회 등)
   - `crawled_at`: 날짜별 조회

2. **컬럼 타입**:
   - `content`: LONGTEXT (최대 4GB, 긴 HTML 대응)
   - `source_url`: VARCHAR(2048) (긴 URL 대응)

3. **불변성 고려**:
   - `@NoArgsConstructor(access = PROTECTED)`: 기본 생성자는 JPA용
   - `@Builder`: 객체 생성 시 명확한 의도 표현

4. **비즈니스 로직**:
   - Entity 내부에 상태 변경 메서드 캡슐화

---

### Step 5: CrawlingJob Entity 작성

```java
package io.github.Hyeonqz.domain.crawling.entity;

import io.github.Hyeonqz.domain.common.BaseTimeEntity;
import io.github.Hyeonqz.domain.crawling.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "crawling_job",
    indexes = {
        @Index(name = "idx_job_name", columnList = "job_name"),
        @Index(name = "idx_started_at", columnList = "started_at"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrawlingJob extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "target_url", length = 2048)
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "fail_count")
    private Integer failCount;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    // 비즈니스 로직 메서드
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

    public boolean isRunning() {
        return this.status == JobStatus.RUNNING;
    }
}
```

---

### Step 6: Repository 인터페이스 작성

#### RawCrawledDataRepository.java
```java
package io.github.Hyeonqz.domain.crawling.repository;

import io.github.Hyeonqz.domain.crawling.entity.RawCrawledData;
import io.github.Hyeonqz.domain.crawling.enums.CrawlingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RawCrawledDataRepository extends JpaRepository<RawCrawledData, Long> {

    // URL로 조회 (중복 체크용)
    Optional<RawCrawledData> findBySourceUrl(String sourceUrl);

    // 상태별 조회
    List<RawCrawledData> findByStatus(CrawlingStatus status);

    // 특정 기간 내 크롤링 데이터 조회
    @Query("SELECT r FROM RawCrawledData r WHERE r.crawledAt BETWEEN :startDate AND :endDate")
    List<RawCrawledData> findByCrawledAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // PENDING 상태인 데이터 개수
    long countByStatus(CrawlingStatus status);

    // 오래된 PENDING 데이터 조회 (재처리용)
    @Query("SELECT r FROM RawCrawledData r WHERE r.status = :status AND r.crawledAt < :threshold ORDER BY r.crawledAt ASC")
    List<RawCrawledData> findOldPendingData(
        @Param("status") CrawlingStatus status,
        @Param("threshold") LocalDateTime threshold
    );
}
```

#### CrawlingJobRepository.java
```java
package io.github.Hyeonqz.domain.crawling.repository;

import io.github.Hyeonqz.domain.crawling.entity.CrawlingJob;
import io.github.Hyeonqz.domain.crawling.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrawlingJobRepository extends JpaRepository<CrawlingJob, Long> {

    // 최근 작업 조회
    Optional<CrawlingJob> findTopByOrderByStartedAtDesc();

    // 상태별 작업 목록
    List<CrawlingJob> findByStatus(JobStatus status);

    // 특정 기간 내 작업 이력
    List<CrawlingJob> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 실행 중인 작업 확인 (중복 실행 방지용)
    @Query("SELECT COUNT(j) > 0 FROM CrawlingJob j WHERE j.status = 'RUNNING'")
    boolean existsRunningJob();

    // 작업 통계
    @Query("SELECT j.status, COUNT(j), AVG(j.successCount) FROM CrawlingJob j GROUP BY j.status")
    List<Object[]> getJobStatistics();
}
```

---

### Step 7: JPA Auditing 설정

**JpaAuditingConfig.java** (invest-domain 모듈):
```java
package io.github.Hyeonqz.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
```

---

### Step 8: DDL 스크립트 생성 (선택)

**schema.sql** (invest-domain/src/main/resources):
```sql
CREATE TABLE IF NOT EXISTS crawling_job (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    target_url VARCHAR(2048),
    status VARCHAR(20) NOT NULL,
    started_at DATETIME NOT NULL,
    completed_at DATETIME,
    total_count INT,
    success_count INT,
    fail_count INT,
    error_message VARCHAR(2000),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_job_name (job_name),
    INDEX idx_started_at (started_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS raw_crawled_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_url VARCHAR(2048) NOT NULL,
    title VARCHAR(500),
    content LONGTEXT NOT NULL,
    content_type VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    crawled_at DATETIME NOT NULL,
    processed_at DATETIME,
    error_message VARCHAR(2000),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_source_url (source_url(255)),
    INDEX idx_status (status),
    INDEX idx_crawled_at (crawled_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 5. 예상 리스크 및 대응 방안

### 리스크 1: content 컬럼 크기 초과
**문제**: 일부 웹페이지는 매우 큰 HTML을 반환할 수 있음

**대응**:
- LONGTEXT 타입 사용 (최대 4GB)
- 크롤링 시 content 길이 체크 후 초과 시 truncate 또는 별도 파일 저장

---

### 리스크 2: URL 중복 체크 성능 이슈
**문제**: `findBySourceUrl` 조회가 빈번할 경우 성능 저하

**대응**:
- `source_url` 컬럼에 인덱스 적용 (이미 설계에 포함)
- URL 해시값 컬럼 추가 고려 (향후 최적화)

---

### 리스크 3: 동시성 이슈 (중복 크롤링)
**문제**: 여러 배치 작업이 동시 실행될 경우 중복 크롤링 발생

**대응**:
- `CrawlingJobRepository.existsRunningJob()` 메서드로 실행 중인 작업 체크
- 배치 작업 시작 시 Lock 메커니즘 적용 (Phase 2에서 구현)

---

### 리스크 4: 트랜잭션 타임아웃
**문제**: 대량 데이터 INSERT 시 트랜잭션 타임아웃 발생 가능

**대응**:
- 배치 작업에서 작은 단위로 나누어 처리 (Chunk 방식)
- `spring.jpa.properties.hibernate.jdbc.batch_size` 설정

---

### 리스크 5: 민감정보 저장
**문제**: 크롤링한 데이터에 개인정보가 포함될 수 있음

**대응**:
- 로깅 시 content 필드 마스킹
- 필요 시 암호화 컬럼 적용 (향후)
- GDPR 준수를 위한 데이터 보관 정책 수립

---

## 6. 테스트 전략

### 6.1 단위 테스트

#### Entity 테스트
```java
@Test
void testRawCrawledData_markAsProcessed() {
    // Given
    RawCrawledData data = RawCrawledData.builder()
        .sourceUrl("https://example.com")
        .content("test content")
        .status(CrawlingStatus.PENDING)
        .crawledAt(LocalDateTime.now())
        .build();

    // When
    data.markAsProcessed();

    // Then
    assertThat(data.getStatus()).isEqualTo(CrawlingStatus.PROCESSED);
    assertThat(data.getProcessedAt()).isNotNull();
}
```

#### Repository 테스트
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // 실제 MySQL 사용
class RawCrawledDataRepositoryTest {

    @Autowired
    private RawCrawledDataRepository repository;

    @Test
    void testFindBySourceUrl() {
        // Given
        String url = "https://test.com";
        RawCrawledData saved = repository.save(
            RawCrawledData.builder()
                .sourceUrl(url)
                .content("content")
                .status(CrawlingStatus.PENDING)
                .crawledAt(LocalDateTime.now())
                .build()
        );

        // When
        Optional<RawCrawledData> found = repository.findBySourceUrl(url);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }
}
```

---

### 6.2 통합 테스트

#### 빌드 테스트
```bash
./gradlew :invest-domain:build
```

**검증 항목**:
- [x] Entity 클래스 컴파일 성공
- [x] QueryDSL Q-Class 생성 확인 (`build/generated/querydsl`)
- [x] Repository 인터페이스 빈 생성 확인
- [x] JPA Auditing 동작 확인

#### 데이터베이스 연동 테스트
```bash
# 로컬 MySQL 실행 (Docker)
docker run --name mysql-test \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=investment \
  -p 3306:3306 \
  -d mysql:8.0

# 테스트 실행
./gradlew :invest-domain:test
```

---

### 6.3 성능 테스트 (향후)

#### 대량 INSERT 테스트
- 10,000건 데이터 INSERT 시간 측정
- 배치 사이즈 최적화 (`hibernate.jdbc.batch_size`)

#### 인덱스 효과 확인
- `EXPLAIN` 쿼리로 인덱스 사용 확인
- `source_url`, `status`, `crawled_at` 인덱스 효과 검증

---

## 7. 작업 체크리스트

### Pre-work
- [x] CLAUDE.md 가이드라인 확인
- [ ] 사용자 승인 획득
- [ ] 로컬 MySQL 데이터베이스 준비

### Implementation
- [ ] Step 1: 패키지 구조 생성
- [ ] Step 2: Enum 클래스 작성 (CrawlingStatus, JobStatus)
- [ ] Step 3: BaseTimeEntity 작성
- [ ] Step 4: RawCrawledData Entity 작성
- [ ] Step 5: CrawlingJob Entity 작성
- [ ] Step 6: Repository 인터페이스 작성
- [ ] Step 7: JPA Auditing 설정
- [ ] Step 8: DDL 스크립트 생성 (선택)

### Verification
- [ ] 전체 빌드 테스트 (`./gradlew :invest-domain:build`)
- [ ] QueryDSL Q-Class 생성 확인
- [ ] Repository 테스트 작성 및 실행
- [ ] Entity 단위 테스트 작성 및 실행
- [ ] MySQL 데이터베이스 테이블 생성 확인

### Documentation
- [ ] `.claude/docs/changes/20260107_crawling-etl-phase1.md` 작성
- [ ] 변경 내역 커밋

---

## 8. 다음 단계 (Phase 2 이후)

### Phase 2: 크롤링 서비스 구현 (invest-external)
- Jsoup 라이브러리로 HTML 파싱
- WebClient로 비동기 HTTP 요청
- 크롤링 결과를 RawCrawledData로 변환

### Phase 3: 배치 작업 구현 (invest-batch)
- Spring Batch Job 구성
- 하루 1회 스케줄링 (`@Scheduled`)
- ETL 파이프라인 구축

### Phase 4: REST API 구현 (invest-api)
- 크롤링 데이터 조회 API
- 작업 이력 조회 API
- 통계 API

### Phase 5: AI 요약 기능 추가
- SummaryData Entity 추가
- AI 요약 서비스 연동
- 요약 결과 저장 및 API 제공

---

## 9. 승인 요청

본 계획을 검토하시고 다음 사항을 확인해주세요:

1. **Entity 설계**: RawCrawledData와 CrawlingJob의 필드 구성이 적절한가요?
2. **패키지 구조**: `io.github.Hyeonqz.domain.crawling` 구조가 괜찮은가요?
3. **인덱스 전략**: 제안된 인덱스가 충분한가요?
4. **Repository 메서드**: 추가로 필요한 쿼리 메서드가 있나요?
5. **크롤링 대상**: 어떤 웹사이트를 크롤링할 예정인가요? (데이터 구조에 영향)

승인하시면 즉시 Step 1부터 순차적으로 구현을 시작하겠습니다.

---

**계획 작성 완료일**: 2026-01-07 23:47
**다음 단계**: 사용자 승인 대기 → Phase 1 Implementation 진행
