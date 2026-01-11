# Implementation Plan: Enterprise-Grade Logback Configuration

## 작업 목표
invest-api 및 invest-batch 모듈에 대기업 IT 표준을 따르는 Logback 설정 구현
- 콘솔 로그 색상 지원
- ELK Stack 통합 대비 JSON 구조화 로깅
- 환경별 프로필 지원 (local, dev, prod)
- 금융 도메인 특성 반영 (민감정보 마스킹, 감사 로깅)

## 현재 상태 분석

### As-Is
- invest-api와 invest-batch 모두 logback-spring.xml 파일 존재하나 빈 상태
- application.yml에서 기본 로깅 설정 관리 중
  - invest-api: DEBUG 레벨 (개발용)
  - invest-batch: INFO 레벨 (배치용)
- 단순 텍스트 포맷 로깅 사용
- ELK Stack 통합 준비 없음

### To-Be
- 환경별 로깅 전략 분리 (local/dev/prod)
- JSON 구조화 로깅으로 ELK Stack 통합 준비
- 콘솔 로그 색상 지원으로 개발 생산성 향상
- 비동기 로깅으로 성능 최적화
- 금융 도메인 특화 로깅 (민감정보 마스킹, 감사 추적)

## 영향받는 컴포넌트

### 수정 대상 파일
1. `/invest-api/src/main/resources/logback-spring.xml` - 신규 작성
2. `/invest-batch/src/main/resources/logback-spring.xml` - 신규 작성

### 추가 검토 필요
- build.gradle.kts - logstash-logback-encoder 의존성 추가 필요 여부
- application.yml - 기존 logging 설정과의 충돌 여부

## 구현 단계별 계획

### Phase 1: 의존성 확인 및 추가
1. 현재 프로젝트의 logback 관련 의존성 확인
2. logstash-logback-encoder 의존성 추가 (ELK Stack 준비)
   - 버전: 7.4 (Spring Boot 3.x 호환)

### Phase 2: invest-api Logback 설정
1. **콘솔 Appender (개발용)**
   - 색상 지원 패턴 적용
   - 상세 로그 포맷 (timestamp, thread, level, logger, message, MDC)

2. **파일 Appender (운영용)**
   - Rolling 정책 (일별, 최대 7일 보관)
   - 크기 제한 (100MB per file)

3. **JSON Appender (ELK 준비)**
   - LogstashEncoder 사용
   - 구조화된 로그 필드
   - 커스텀 필드: application, profile, hostname

4. **비동기 Appender**
   - 성능 최적화를 위한 AsyncAppender 적용
   - Queue size: 256

5. **프로필별 설정**
   - local: 콘솔(색상) + 파일
   - dev: 콘솔 + 파일 + JSON
   - prod: 파일 + JSON (콘솔 최소화)

6. **로거 레벨 설정**
   - Application: DEBUG (local), INFO (dev/prod)
   - Hibernate SQL: DEBUG (local/dev), WARN (prod)
   - Spring Framework: INFO
   - 외부 라이브러리: WARN

### Phase 3: invest-batch Logback 설정
1. **배치 특화 설정**
   - Job/Step 실행 로그 구분
   - Chunk 처리 로그
   - 실패/재시도 로그 강조

2. **파일 분리 전략**
   - 일반 로그: logs/batch-application.log
   - 에러 로그: logs/batch-error.log
   - 감사 로그: logs/batch-audit.log

3. **스케줄러 로깅**
   - Quartz 스케줄러 실행 로그
   - Job 실행 시간 추적

4. **프로필별 설정** (invest-api와 동일)

### Phase 4: 금융 도메인 특화 설정
1. **민감정보 마스킹**
   - 패스워드, 토큰, 개인정보 마스킹 패턴 적용
   - Custom MaskingConverter 구현 검토

2. **감사 로깅**
   - 별도 Appender로 감사 로그 분리
   - 무결성 보장 (별도 파일, 암호화 검토)

3. **트랜잭션 추적**
   - MDC를 통한 Transaction ID 추적
   - 요청-응답 상관관계 추적

### Phase 5: 검증 및 테스트
1. 각 프로필별 로그 출력 확인
2. 파일 Rolling 동작 확인
3. JSON 포맷 유효성 검증
4. 성능 테스트 (비동기 로깅 효과)

## 예상 리스크 및 대응 방안

### Risk 1: 기존 application.yml 로깅 설정과 충돌
- **대응**: logback-spring.xml이 우선순위가 높으므로 application.yml의 logging 설정 주석 처리 또는 제거

### Risk 2: 의존성 버전 충돌
- **대응**: Spring Boot BOM에서 관리하는 버전 사용, 필요시 명시적 버전 지정

### Risk 3: 비동기 로깅 시 로그 유실 가능성
- **대응**: gracefulShutdown 설정, Queue size 적절히 설정 (256-512)

### Risk 4: JSON 로깅 파일 크기 증가
- **대응**: 적절한 Rolling Policy 설정, 압축 활성화

### Risk 5: MDC 컨텍스트 누락 (멀티스레드 환경)
- **대응**: InheritableThreadLocal 사용, Spring Sleuth/Micrometer Tracing 고려

## 테스트 전략

### 단위 테스트
- 로그 설정 로드 테스트
- 각 Appender 동작 확인
- 프로필별 설정 전환 확인

### 통합 테스트
1. **로컬 환경 테스트**
   - 콘솔 색상 출력 확인
   - 파일 생성 및 Rolling 확인

2. **JSON 로그 검증**
   - JSON 파싱 가능 여부
   - 필수 필드 포함 여부
   - 구조 일관성

3. **성능 테스트**
   - 동기 vs 비동기 로깅 성능 비교
   - 대량 로그 발생 시 시스템 영향도

4. **민감정보 마스킹 검증**
   - 패스워드, 토큰 등 마스킹 확인
   - 정규표현식 패턴 정확도

### 운영 환경 준비
- ELK Stack 미설치 상태에서도 정상 동작 확인
- Logstash 연동 시나리오 문서화
- 모니터링 대시보드 설계 (향후)

## ELK Stack 통합 로드맵

### 현재 단계: Phase 1 (준비)
- JSON 구조화 로깅 설정
- logstash-logback-encoder 의존성 추가

### Phase 2: Logstash 연동 (향후)
- LogstashTcpSocketAppender 설정
- Logstash 파이프라인 구성
- 필터 및 인덱스 전략 수립

### Phase 3: Elasticsearch & Kibana (향후)
- 인덱스 템플릿 설계
- 대시보드 구성
- 알림 규칙 설정

## 참고 자료

### Best Practices
- [Spring Boot Logs Aggregation with ELK Stack](https://auth0.com/blog/spring-boot-logs-aggregation-and-monitoring-using-elk-stack/)
- [A Complete Guide to Logging in Java Microservices](https://www.springfuse.com/logging-in-distributed-systems/)
- [Building Robust ELK Integration with Spring Boot](https://dev.to/devaaai/building-a-robust-elk-integration-with-spring-boot-microservices-1gc1)

### Technical Documentation
- [Configuring JSON Logs with Logback](https://tech.asimio.net/2023/08/01/Formatting-JSON-Logs-in-Spring-Boot-2-applications-with-Slf4j-Logback-and-Logstash.html)
- [Logstash Logback Encoder GitHub](https://github.com/logfellow/logstash-logback-encoder)
- [Baeldung - Java Logs to ELK](https://www.baeldung.com/java-application-logs-to-elastic-stack)

## 의사결정 필요 사항

1. **logstash-logback-encoder 의존성 추가 여부**
   - 추가하면 바로 ELK 준비 완료, 미추가 시 기본 JSON 인코더 사용
   - 권장: 추가 (향후 확장성)

2. **민감정보 마스킹 수준**
   - 기본 패턴 마스킹 vs Custom Converter 구현
   - 권장: 1단계는 기본 패턴, 필요시 Custom Converter 추가

3. **비동기 로깅 적용 범위**
   - 모든 Appender vs 파일/JSON만
   - 권장: 파일/JSON에만 적용, 콘솔은 동기 유지

4. **로그 보관 정책**
   - 일반 로그: 7일 / 에러 로그: 30일 / 감사 로그: 90일
   - 권장: 위 정책 적용 (법적 요구사항 확인 필요)

## 승인 후 진행 순서
1. build.gradle.kts 의존성 확인 및 추가
2. invest-api logback-spring.xml 작성
3. invest-batch logback-spring.xml 작성
4. 로컬 환경 테스트
5. 변경사항 문서화 (CLAUDE.md 지침에 따름)

---

**작성일**: 2026-01-08 00:01:37
**작성자**: Claude Code
**예상 소요 시간**: N/A (사용자 판단)
**우선순위**: High (운영 환경 필수 설정)