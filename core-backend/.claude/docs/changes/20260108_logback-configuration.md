# Change Log: Enterprise-Grade Logback Configuration Implementation

**작성일**: 2026-01-08
**작성자**: Claude Code
**관련 모듈**: invest-api, invest-batch
**변경 유형**: 로깅 인프라 개선

---

## As-Is (변경 전)

### 현재 구조
1. **로깅 설정 위치**
   - `logback-spring.xml` 파일이 존재하나 빈 상태
   - `application.yml`에서 기본 로깅 설정 관리

2. **invest-api 로깅 설정** (`application.yml`)
   ```yaml
   logging:
     level:
       io.github.Hyeonqz: DEBUG
       org.hibernate.SQL: DEBUG
       org.hibernate.type.descriptor.sql.BasicBinder: TRACE
     pattern:
       console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
   ```

3. **invest-batch 로깅 설정** (`application.yml`)
   ```yaml
   logging:
     level:
       io.github.Hyeonqz: INFO
       org.springframework.batch: INFO
       org.springframework.scheduling: DEBUG
     pattern:
       console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
   ```

### 문제점
- **색상 지원 없음**: 콘솔 로그 가독성 저하
- **구조화 로깅 부재**: 텍스트 기반 로그만 지원, 파싱 어려움
- **환경별 설정 미분리**: local/dev/prod 환경 구분 없음
- **ELK Stack 준비 안됨**: JSON 포맷 로깅 미지원
- **로그 보관 정책 없음**: 파일 Rolling 설정 부재
- **성능 최적화 부족**: 동기 로깅으로 인한 성능 저하 가능성
- **감사 로그 미분리**: 일반 로그와 감사 로그 혼재
- **배치 특화 로깅 없음**: Job/Step 실행 추적 어려움

---

## To-Be (변경 후)

### 변경된 구조

#### 1. 의존성 추가
**파일**: `build.gradle.kts` (root)
```kotlin
// Logging
implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
implementation("net.logstash.logback:logstash-logback-encoder:7.4")  // 추가
```

#### 2. invest-api Logback 설정

**파일**: `invest-api/src/main/resources/logback-spring.xml`

**주요 기능**:
1. **Appender 구성**
   - `CONSOLE`: 색상 지원 콘솔 로그 (개발 환경)
   - `FILE`: 일반 애플리케이션 로그 (Rolling)
   - `ERROR_FILE`: 에러 전용 로그
   - `JSON_FILE`: JSON 구조화 로그 (ELK 준비)
   - `AUDIT_FILE`: 감사 로그 전용
   - `ASYNC_*`: 비동기 Appender (성능 최적화)

2. **프로필별 설정**
   - **local**: DEBUG 레벨, 콘솔(색상) + 파일 로깅
   - **dev**: INFO 레벨, 콘솔 + 파일 + JSON 로깅
   - **prod**: INFO 레벨, 파일 + JSON만 (콘솔 최소화)

3. **Rolling Policy**
   - 파일 크기: 최대 100MB
   - 보관 기간: 90일
   - 압축: .gz 포맷
   - 전체 용량 제한: 10GB

4. **로거 설정**
   ```xml
   <!-- Application -->
   <logger name="io.github.Hyeonqz" level="DEBUG"/>

   <!-- Audit (감사 로그) -->
   <logger name="io.github.Hyeonqz.audit" level="INFO"/>

   <!-- Hibernate SQL -->
   <logger name="org.hibernate.SQL" level="DEBUG"/>
   ```

#### 3. invest-batch Logback 설정

**파일**: `invest-batch/src/main/resources/logback-spring.xml`

**배치 특화 기능**:
1. **Appender 구성**
   - `BATCH_FILE`: 일반 배치 로그
   - `BATCH_ERROR_FILE`: 에러 전용 로그
   - `BATCH_JOB_FILE`: Job 실행 이력 전용
   - `JSON_FILE`: JSON 구조화 로그
   - `AUDIT_FILE`: 감사 로그
   - `ASYNC_*`: 비동기 Appender

2. **Job/Step 추적**
   - MDC를 통한 `jobName`, `stepName` 추적
   - 패턴: `[jobName:stepName]` 형식으로 표시

3. **배치 전용 로거**
   ```xml
   <!-- Batch Job Logger -->
   <logger name="io.github.Hyeonqz.batch.job" level="INFO"/>

   <!-- Spring Batch -->
   <logger name="org.springframework.batch" level="INFO"/>
   <logger name="org.springframework.batch.item" level="DEBUG"/>

   <!-- Quartz Scheduler -->
   <logger name="org.springframework.scheduling" level="DEBUG"/>
   <logger name="org.quartz" level="INFO"/>
   ```

### 개선 효과

1. **가독성 향상**
   - ANSI 색상 코드로 콘솔 로그 가독성 대폭 개선
   - timestamp, thread, traceId, level, logger, message 구조화

2. **ELK Stack 준비 완료**
   - JSON 포맷 로그 생성 (LogstashEncoder)
   - 커스텀 필드: application, service_type, log_type
   - Logstash 연동 시 즉시 사용 가능

3. **환경별 최적화**
   - local: 상세 로그 + 색상 (개발 효율성)
   - dev: 중간 레벨 + JSON (통합 테스트)
   - prod: 최소 콘솔 + 파일/JSON (성능 우선)

4. **성능 최적화**
   - AsyncAppender 적용 (Queue size: 512)
   - 로깅으로 인한 애플리케이션 성능 저하 최소화
   - Non-blocking 로그 처리

5. **로그 관리 자동화**
   - 일별 Rolling (SizeAndTimeBasedRollingPolicy)
   - 90일 자동 삭제
   - .gz 압축으로 스토리지 절약

6. **감사 추적 강화**
   - 별도 audit 로거 및 파일
   - 금융 도메인 컴플라이언스 대응

7. **배치 운영성 향상**
   - Job/Step 실행 로그 분리
   - 에러 로그 별도 추적
   - Quartz 스케줄러 로그 모니터링

---

## Impact Analysis

### 영향받는 API 엔드포인트
- **영향 없음**: 로깅 설정 변경만으로 API 동작에는 영향 없음
- **주의사항**: 로그 레벨 변경으로 인한 로그 볼륨 변화 가능

### 데이터베이스 스키마 변경
- **변경 없음**

### 애플리케이션 동작 변경

#### 1. application.yml 설정 우선순위
- **변경 전**: `application.yml`의 `logging.*` 설정 사용
- **변경 후**: `logback-spring.xml`이 우선 적용됨
- **권장사항**: `application.yml`의 `logging.*` 설정 제거 또는 주석 처리

#### 2. 로그 파일 생성 위치
- **기본 경로**: `logs/` 디렉토리
- **생성 파일**:
  - **invest-api**:
    - `invest-api.log` (일반 로그)
    - `invest-api-error.log` (에러만)
    - `invest-api-json.log` (JSON 포맷)
    - `invest-api-audit.log` (감사 로그)
  - **invest-batch**:
    - `invest-batch.log` (일반 로그)
    - `invest-batch-error.log` (에러만)
    - `invest-batch-job.log` (Job 실행 이력)
    - `invest-batch-json.log` (JSON 포맷)
    - `invest-batch-audit.log` (감사 로그)

#### 3. 로그 레벨 변경
- **local 환경**: 기존과 동일 (DEBUG)
- **dev/prod 환경**: INFO 레벨로 통일
- **Hibernate SQL**: prod에서 WARN으로 변경

### 호환성 이슈

#### 1. Spring Boot 버전
- **요구사항**: Spring Boot 3.x
- **현재 버전**: 3.5.3 ✅ 호환
- **logstash-logback-encoder 7.4**: Spring Boot 3.x 완전 호환

#### 2. 기존 로그 수집 도구
- 기존 텍스트 기반 로그 파서와 호환 유지
- JSON 로그는 별도 파일로 분리되어 영향 없음

### 운영 고려사항

#### 1. 디스크 사용량
- **예상 증가**: JSON 로그 추가로 20-30% 증가 가능
- **대응**: 90일 보관 정책 + .gz 압축으로 완화
- **모니터링**: 디스크 사용량 주기적 확인 필요

#### 2. 로그 볼륨
- **local 환경**: DEBUG 레벨로 인한 많은 로그 생성
- **prod 환경**: INFO 레벨로 적절한 로그 볼륨 유지

#### 3. 성능 영향
- AsyncAppender로 성능 영향 최소화
- Queue 크기 512: 초당 수천 건의 로그 처리 가능
- **벤치마크 필요**: 실제 운영 환경에서 성능 테스트 권장

### 롤백 계획

#### 방법 1: 파일 삭제
```bash
rm invest-api/src/main/resources/logback-spring.xml
rm invest-batch/src/main/resources/logback-spring.xml
```
- application.yml의 logging 설정이 다시 적용됨

#### 방법 2: 의존성 제거 (필요시)
```kotlin
// build.gradle.kts에서 제거
// implementation("net.logstash.logback:logstash-logback-encoder:7.4")
```

#### 방법 3: 프로필별 비활성화
```bash
# 특정 환경에서만 기존 설정 사용
# application-prod.yml에서 logging.config 설정 가능
```

---

## Testing

### 로컬 환경 테스트 체크리스트

#### 1. 콘솔 색상 출력 확인
```bash
cd invest-api
./gradlew bootRun

# 확인사항:
# - 콘솔에 색상이 정상 표시되는지
# - timestamp, thread, traceId, level, logger, message 모두 출력되는지
# - DEBUG 레벨 로그가 출력되는지
```

#### 2. 파일 생성 확인
```bash
# 애플리케이션 실행 후
ls -lh logs/

# 확인사항:
# - invest-api.log 파일 생성
# - invest-api-error.log 파일 생성
# - invest-api-json.log 파일 생성
# - invest-api-audit.log 파일 생성
```

#### 3. JSON 로그 포맷 검증
```bash
# JSON 로그 파일 내용 확인
cat logs/invest-api-json.log | jq .

# 확인사항:
# - JSON 파싱이 정상적으로 되는지
# - 필수 필드 포함 여부: timestamp, message, logger, level, thread
# - 커스텀 필드: application, service_type
```

#### 4. Rolling Policy 테스트
```bash
# 대용량 로그 생성 (테스트용)
# - 로그를 많이 발생시켜 Rolling이 정상 동작하는지 확인
# - 100MB 초과 시 새 파일 생성 확인
# - 날짜 변경 시 새 파일 생성 확인
```

#### 5. 프로필별 설정 확인
```bash
# Dev 프로필로 실행
./gradlew bootRun --args='--spring.profiles.active=dev'

# 확인사항:
# - INFO 레벨 로그 출력
# - JSON 파일 생성 여부

# Prod 프로필로 실행 (로컬 테스트)
./gradlew bootRun --args='--spring.profiles.active=prod'

# 확인사항:
# - 콘솔 로그 최소화
# - 파일 및 JSON 로그만 생성
```

### 통합 테스트 시나리오

#### invest-api 테스트
1. **API 요청 로깅**
   ```kotlin
   @RestController
   class TestController {
       private val logger = KotlinLogging.logger {}

       @GetMapping("/test")
       fun test() {
           logger.debug { "DEBUG 로그 테스트" }
           logger.info { "INFO 로그 테스트" }
           logger.warn { "WARN 로그 테스트" }
           logger.error { "ERROR 로그 테스트" }
       }
   }
   ```

2. **감사 로그 테스트**
   ```kotlin
   private val auditLogger = KotlinLogging.logger("io.github.Hyeonqz.audit")

   auditLogger.info { "사용자 로그인 성공: userId=12345" }

   # 확인: logs/invest-api-audit.log 파일에 기록되는지
   ```

3. **에러 로그 분리 확인**
   ```kotlin
   try {
       throw RuntimeException("테스트 에러")
   } catch (e: Exception) {
       logger.error(e) { "에러 발생" }
   }

   # 확인: logs/invest-api-error.log에만 기록되는지
   ```

#### invest-batch 테스트
1. **Job 실행 로그**
   ```kotlin
   // MDC에 jobName, stepName 설정
   MDC.put("jobName", "testJob")
   MDC.put("stepName", "step1")

   logger.info { "Batch Job 시작" }

   # 확인: 로그에 [testJob:step1] 형식으로 표시되는지
   ```

2. **Job 전용 로거 테스트**
   ```kotlin
   private val jobLogger = KotlinLogging.logger("io.github.Hyeonqz.batch.job")

   jobLogger.info { "Job 실행 완료: jobId=1, status=SUCCESS" }

   # 확인: logs/invest-batch-job.log 파일에 기록되는지
   ```

3. **Quartz 스케줄러 로그**
   ```bash
   # Quartz Job 실행
   # 확인: org.springframework.scheduling 로그가 DEBUG 레벨로 출력되는지
   ```

### 성능 테스트

#### 비동기 로깅 효과 측정
```kotlin
@Test
fun `비동기 로깅 성능 테스트`() {
    val iterations = 100_000

    val startTime = System.currentTimeMillis()
    repeat(iterations) {
        logger.info { "Performance test log message $it" }
    }
    val endTime = System.currentTimeMillis()

    println("10만 건 로그 처리 시간: ${endTime - startTime}ms")
    // 예상: AsyncAppender 사용 시 100-200ms 이내
}
```

### 운영 환경 배포 전 체크리스트

- [ ] `logs/` 디렉토리 생성 및 쓰기 권한 확인
- [ ] 디스크 용량 충분한지 확인 (최소 50GB 권장)
- [ ] logstash-logback-encoder 의존성 정상 포함 확인
- [ ] 프로필별 설정 검증 (local/dev/prod)
- [ ] 기존 모니터링 도구와 호환성 확인
- [ ] 로그 로테이션 정책 검토
- [ ] 백업 및 아카이빙 전략 수립
- [ ] ELK Stack 연동 계획 검토 (향후)

### 모니터링 항목

#### 애플리케이션 시작 시
- Logback 설정 파일 로드 확인
- Appender 초기화 성공 여부
- 파일 생성 권한 문제 없는지

#### 운영 중
- 로그 파일 크기 증가율
- Rolling Policy 정상 동작 여부
- AsyncAppender Queue 상태 (overflow 발생 여부)
- 디스크 사용량 트렌드

#### 장애 대응
- 로그 파일 생성 실패 시 알림
- 디스크 용량 부족 알림 (임계값: 90%)
- 로그 레벨 동적 변경 필요 시 대응 방법

---

## 향후 개선 사항

### Phase 2: ELK Stack 통합 (향후)
1. **Logstash 연동**
   ```xml
   <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
       <destination>logstash-server:5000</destination>
       <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
   </appender>
   ```

2. **Elasticsearch 인덱싱 전략**
   - 일별 인덱스: `invest-api-logs-2026.01.08`
   - 인덱스 템플릿 정의
   - ILM (Index Lifecycle Management) 정책

3. **Kibana 대시보드**
   - API 응답 시간 분석
   - 에러율 모니터링
   - 배치 Job 실행 이력

### Phase 3: 민감정보 마스킹 강화
```kotlin
// Custom MaskingConverter 구현
class SensitiveDataMaskingConverter : MessageConverter() {
    override fun convert(event: ILoggingEvent): String {
        return event.formattedMessage
            .replace(Regex("password=\\w+"), "password=***")
            .replace(Regex("token=\\w+"), "token=***")
            .replace(Regex("\\d{13,16}"), "****-****-****") // 카드번호
    }
}
```

### Phase 4: 분산 추적 (Distributed Tracing)
```kotlin
// Spring Cloud Sleuth / Micrometer Tracing 통합
implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
implementation("io.micrometer:micrometer-tracing-bridge-brave")
```

---

## 참고 자료

### 구현 가이드
- [Spring Boot Logs Aggregation and Monitoring Using ELK Stack](https://auth0.com/blog/spring-boot-logs-aggregation-and-monitoring-using-elk-stack/)
- [A Complete Guide to Logging in Java Microservices with ELK Stack](https://www.springfuse.com/logging-in-distributed-systems/)
- [Building a Robust ELK Integration with Spring Boot Microservices](https://dev.to/devaaai/building-a-robust-elk-integration-with-spring-boot-microservices-1gc1)

### 기술 문서
- [Configuring JSON-Formatted Logs with Logback and Logstash](https://tech.asimio.net/2023/08/01/Formatting-JSON-Logs-in-Spring-Boot-2-applications-with-Slf4j-Logback-and-Logstash.html)
- [Logstash Logback Encoder GitHub](https://github.com/logfellow/logstash-logback-encoder)
- [Send the Logs of a Java App to the Elastic Stack (ELK) | Baeldung](https://www.baeldung.com/java-application-logs-to-elastic-stack)

### 모범사례
- [Structured Logging with Structured Arguments – INNOQ](https://www.innoq.com/en/blog/2019/05/structured-logging/)
- [JSON Log Files (Structured Logging) with Spring Boot and ELK Stack](https://medium.com/@IlyasKeser/json-log-files-structured-logging-with-spring-boot-and-elk-stack-96f47a57a02b)

---

**변경사항 승인자**: (승인 후 기록)
**배포 일시**: (배포 후 기록)
**검증 담당자**: (검증 후 기록)
