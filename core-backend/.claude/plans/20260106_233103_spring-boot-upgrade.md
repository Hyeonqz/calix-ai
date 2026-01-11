# Spring Boot 4.0.1 멀티모듈 적용 계획

**작성일**: 2026-01-06 23:31
**작업자**: Claude Code
**검토 상태**: 대기 중

---

## 1. 작업 목표 및 범위

### 목표
- 현재 순수 Kotlin/Java 멀티모듈 프로젝트에 Spring Boot 4.0.1을 전체 모듈에 적용
- invest-domain 모듈은 Java 언어 유지 (Entity 관리 목적)
- 금융 투자 서비스 백엔드의 기반 프레임워크 구축

### 범위
**대상 모듈 (5개)**:
- `invest-api` (Kotlin): REST API 제공 계층
- `invest-batch` (Kotlin): 배치 작업 처리
- `invest-domain` (Java): Entity 및 도메인 모델 관리
- `invest-external` (Kotlin): 외부 시스템 연동
- `module-shared` (Kotlin): 공통 유틸리티 및 설정

### 제외 범위
- 비즈니스 로직 구현 (프레임워크 설정만 진행)
- 데이터베이스 스키마 마이그레이션
- 배포 파이프라인 설정

---

## 2. 현재 상태 분석

### 프로젝트 구조
```
core-backend/
├── build.gradle.kts (Root)
├── settings.gradle.kts
├── gradle.properties
├── invest-api/
│   └── build.gradle.kts (Kotlin)
├── invest-batch/
│   └── build.gradle.kts (Kotlin)
├── invest-domain/
│   └── build.gradle.kts (Java) ✓
├── invest-external/
│   └── build.gradle.kts (Kotlin)
└── module-shared/
    └── build.gradle.kts (Kotlin)
```

### 현재 설정
- **Kotlin 버전**: 2.2.21
- **JVM 버전**: 21
- **Spring Boot**: 미적용
- **빌드 도구**: Gradle (Kotlin DSL)

### 주의사항
⚠️ **Spring Boot 4.0.1 버전 확인 필요**
2026년 1월 현재, Spring Boot 공식 최신 버전은 3.x 대입니다. Spring Boot 4.0.1이 실제로 릴리스되었는지 확인이 필요하며, 릴리스되지 않은 경우 다음 대안을 고려해야 합니다:
- Spring Boot 3.4.x (최신 안정 버전)
- Spring Boot 4.0.0-M1 (마일스톤/RC 버전)

---

## 3. 영향받는 컴포넌트

### 3.1 빌드 설정 파일
- [수정] `build.gradle.kts` (Root)
- [수정] `invest-api/build.gradle.kts`
- [수정] `invest-batch/build.gradle.kts`
- [수정] `invest-domain/build.gradle.kts` (Java 유지)
- [수정] `invest-external/build.gradle.kts`
- [수정] `module-shared/build.gradle.kts`
- [선택] `gradle.properties` (버전 관리 옵션)

### 3.2 모듈별 역할 및 필요 의존성

#### invest-api (Kotlin)
**역할**: REST API 엔드포인트 제공
**필요 의존성**:
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-actuator`
- `com.fasterxml.jackson.module:jackson-module-kotlin`
- `invest-domain` (모듈 의존)
- `module-shared` (모듈 의존)

#### invest-batch (Kotlin)
**역할**: 스케줄링 및 배치 작업
**필요 의존성**:
- `spring-boot-starter-batch`
- `spring-boot-starter-quartz` (선택)
- `invest-domain` (모듈 의존)
- `module-shared` (모듈 의존)

#### invest-domain (Java)
**역할**: Entity, Repository, 도메인 로직
**필요 의존성**:
- `spring-boot-starter-data-jpa`
- `mysql-connector-j` (MySQL 8.0 드라이버)
- `com.querydsl:querydsl-jpa` (QueryDSL)
- `com.querydsl:querydsl-apt` (QueryDSL APT)

#### invest-external (Kotlin)
**역할**: 외부 API 연동
**필요 의존성**:
- `spring-boot-starter-webflux` (WebClient)
- `module-shared` (모듈 의존)

#### module-shared (Kotlin)
**역할**: 공통 유틸리티, 설정, 예외 처리
**필요 의존성**:
- `spring-boot-starter`
- `spring-boot-configuration-processor` (선택)

---

## 4. 구현 단계별 상세 계획

### Phase 1: Root 빌드 설정 (build.gradle.kts)

#### 작업 내용
1. **Plugin 추가**
   ```kotlin
   plugins {
       kotlin("jvm") version "2.2.21"
       kotlin("plugin.spring") version "2.2.21" apply false
       kotlin("plugin.jpa") version "2.2.21" apply false
       id("org.springframework.boot") version "4.0.1" apply false
       id("io.spring.dependency-management") version "1.1.6" apply false
   }
   ```

2. **Subprojects 공통 설정**
   - Spring Boot Dependency Management 적용
   - Kotlin 관련 옵션 설정 (`-Xjsr305=strict`)
   - 공통 의존성 (testing, logging 등)

3. **버전 관리 전략**
   - `ext` 속성 또는 `gradle.properties`에 주요 버전 변수 관리
   - Kotlin, Spring Boot, MySQL Connector 등

---

### Phase 2: invest-domain (Java) 설정

#### 작업 내용
1. **Plugin 적용**
   ```kotlin
   plugins {
       id("java")
       id("org.springframework.boot")
       id("io.spring.dependency-management")
       kotlin("plugin.jpa")  // JPA 지원
   }
   ```

2. **의존성 추가**
   - JPA, MySQL, QueryDSL
   - Lombok (선택, Java 개발 편의성)

3. **Java Compiler 설정**
   ```kotlin
   java {
       sourceCompatibility = JavaVersion.VERSION_21
       targetCompatibility = JavaVersion.VERSION_21
   }
   ```

4. **QueryDSL 설정**
   - APT 프로세서 설정
   - Q-Class 생성 경로 지정

#### 주의사항
- **Java-Kotlin 상호운용성**: Entity가 Java로 작성되므로 Kotlin 모듈에서 접근 시 Null-Safety 주의
- **JPA Entity 규약**: 기본 생성자, Getter/Setter 필요 (Lombok 활용 권장)

---

### Phase 3: Kotlin 모듈 설정 (invest-api, invest-batch, invest-external, module-shared)

#### 작업 내용 (공통)
1. **Plugin 적용**
   ```kotlin
   plugins {
       kotlin("jvm")
       kotlin("plugin.spring")
       id("org.springframework.boot")
       id("io.spring.dependency-management")
   }
   ```

2. **Kotlin Compiler 옵션**
   ```kotlin
   kotlin {
       compilerOptions {
           freeCompilerArgs.add("-Xjsr305=strict")
       }
   }
   ```

3. **bootJar 설정**
   - `invest-api`, `invest-batch`: 실행 가능한 jar 생성 (`enabled = true`)
   - `invest-external`, `module-shared`: 라이브러리 모듈 (`enabled = false`)

#### 모듈별 특화 의존성
각 모듈은 위 "3.2 모듈별 역할 및 필요 의존성" 참조

---

### Phase 4: Application 진입점 생성

#### invest-api
```kotlin
package com.company.investment.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.company.investment"])
class InvestApiApplication

fun main(args: Array<String>) {
    runApplication<InvestApiApplication>(*args)
}
```

#### invest-batch
```kotlin
package com.company.investment.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["com.company.investment"])
class InvestBatchApplication

fun main(args: Array<String>) {
    runApplication<InvestBatchApplication>(*args)
}
```

---

### Phase 5: 설정 파일 생성

#### invest-api/src/main/resources/application.yml
```yaml
spring:
  application:
    name: invest-api
  datasource:
    url: jdbc:mysql://localhost:3306/investment?useSSL=false&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:invest_user}
    password: ${DB_PASSWORD:invest_pass}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
    show-sql: false

server:
  port: 8080

logging:
  level:
    com.company.investment: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

#### invest-batch/src/main/resources/application.yml
```yaml
spring:
  application:
    name: invest-batch
  datasource:
    url: jdbc:mysql://localhost:3306/investment?useSSL=false&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:invest_user}
    password: ${DB_PASSWORD:invest_pass}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
  batch:
    jdbc:
      initialize-schema: never
    job:
      enabled: false

logging:
  level:
    com.company.investment: INFO
```

---

## 5. 예상 리스크 및 대응 방안

### 리스크 1: Spring Boot 4.0.1 버전 미존재
**확률**: 높음
**영향도**: 높음

**대응 방안**:
1. Spring Boot 공식 릴리스 확인 (https://spring.io/projects/spring-boot)
2. 대안 버전 선택:
   - Option A: Spring Boot 3.4.x (권장, 안정 버전)
   - Option B: Spring Boot 4.0.0-SNAPSHOT (비권장, 불안정)
3. 사용자 승인 후 버전 변경

---

### 리스크 2: Kotlin 2.2.21 - Spring Boot 호환성
**확률**: 중간
**영향도**: 중간

**대응 방안**:
1. Spring Boot 공식 권장 Kotlin 버전 확인
2. 호환성 이슈 발생 시 Kotlin 버전 다운그레이드 고려 (예: 1.9.x)
3. 빌드 테스트 후 확인

---

### 리스크 3: Java-Kotlin 모듈 간 상호운용성
**확률**: 중간
**영향도**: 중간

**대응 방안**:
- Java Entity에 `@JvmOverloads`, `@JvmStatic` 등 어노테이션 활용
- Kotlin에서 Java Entity 사용 시 Null-Safety 명시적 처리
- 통합 테스트로 상호운용성 검증

---

### 리스크 4: QueryDSL APT 설정 복잡도
**확률**: 중간
**영향도**: 낮음

**대응 방안**:
- Gradle Kotlin DSL 기반 QueryDSL 설정 템플릿 활용
- Q-Class 생성 경로를 `build/generated` 하위로 명확히 지정
- IntelliJ IDEA에서 Generated Sources Root로 마킹

---

### 리스크 5: 금융 도메인 보안 요구사항
**확률**: 낮음 (현 단계)
**영향도**: 높음 (향후)

**대응 방안**:
- Spring Security 적용은 별도 작업으로 분리
- 현재는 기본 설정만 적용 (actuator endpoint 보호 등)
- application.yml에서 민감정보 환경변수 처리 (${DB_PASSWORD})

---

## 6. 테스트 전략

### 6.1 빌드 검증
```bash
# 전체 모듈 빌드
./gradlew clean build

# 의존성 트리 확인
./gradlew dependencies

# 각 모듈별 빌드
./gradlew :invest-api:build
./gradlew :invest-batch:build
./gradlew :invest-domain:build
```

### 6.2 Application 실행 테스트
```bash
# invest-api 실행
./gradlew :invest-api:bootRun

# invest-batch 실행
./gradlew :invest-batch:bootRun
```

### 6.3 검증 항목
- [ ] 모든 모듈 빌드 성공
- [ ] invest-api 정상 기동 (포트 8080)
- [ ] invest-batch 정상 기동
- [ ] Actuator health endpoint 응답 확인 (`http://localhost:8080/actuator/health`)
- [ ] 로그에 Spring Boot 배너 출력 확인
- [ ] JPA EntityManager 빈 생성 확인 (invest-domain)
- [ ] Kotlin-Java 모듈 간 의존성 정상 작동

### 6.4 롤백 계획
- Git 커밋 이전 상태로 복구
- 백업된 `build.gradle.kts` 파일 복원

---

## 7. 작업 체크리스트

### Pre-work
- [ ] Spring Boot 4.0.1 버전 존재 여부 확인
- [ ] 사용자 최종 승인 획득
- [ ] 현재 코드 Git 커밋 (백업 목적)

### Implementation
- [ ] Phase 1: Root build.gradle.kts 수정
- [ ] Phase 2: invest-domain build.gradle.kts 수정
- [ ] Phase 3-1: invest-api build.gradle.kts 수정
- [ ] Phase 3-2: invest-batch build.gradle.kts 수정
- [ ] Phase 3-3: invest-external build.gradle.kts 수정
- [ ] Phase 3-4: module-shared build.gradle.kts 수정
- [ ] Phase 4: Application 진입점 생성
- [ ] Phase 5: application.yml 생성

### Verification
- [ ] 전체 빌드 테스트 (`./gradlew clean build`)
- [ ] invest-api 기동 테스트
- [ ] invest-batch 기동 테스트
- [ ] Actuator 엔드포인트 확인
- [ ] 로그 정상 출력 확인

### Documentation
- [ ] `.claude/docs/changes/20260106_spring-boot-upgrade.md` 작성
- [ ] 변경 내역 커밋 메시지 작성
- [ ] 사용자에게 완료 보고

---

## 8. 예상 소요 시간

- Phase 1: Root 빌드 설정 - 15분
- Phase 2: invest-domain 설정 - 20분
- Phase 3: Kotlin 모듈 설정 - 40분
- Phase 4: Application 진입점 - 15분
- Phase 5: 설정 파일 생성 - 20분
- 테스트 및 검증 - 30분
- 문서화 - 20분

**총 예상 시간**: 약 2.5시간

---

## 9. 추가 고려사항

### 9.1 향후 확장 계획
- Spring Security 적용 (인증/인가)
- Spring Cloud Config (설정 외부화)
- Observability (Micrometer, Prometheus)
- Docker/Kubernetes 배포 설정

### 9.2 Kotlin 개발자 학습 가이드
사용자(Java/Spring Boot 개발자)를 위한 Kotlin 포인터:
- **Data Class**: `data class UserDto(...)`는 Java의 Lombok `@Data`와 유사
- **Null Safety**: `String?`(nullable) vs `String`(non-null)
- **Extension Functions**: `fun String.toUpperCase()`는 정적 메서드와 유사
- **코루틴**: 비동기 처리 시 `suspend fun`과 `runBlocking`, `async` 활용
- **상호운용성**: Kotlin에서 Java 코드 호출은 자연스럽게 가능

---

## 10. 승인 요청

본 계획을 검토하시고 다음 사항을 확인해주세요:

1. **Spring Boot 버전**: 4.0.1이 맞습니까? 존재하지 않을 경우 3.4.x로 변경해도 괜찮습니까?
2. **모듈 역할**: 각 모듈의 역할 및 의존성이 적절합니까?
3. **invest-domain Java 유지**: Entity 모듈을 Java로 유지하는 것이 확정입니까?
4. **추가 요구사항**: 특정 Spring Boot Starter나 라이브러리가 추가로 필요합니까?
5. **데이터베이스**: MySQL 8.0 외에 다른 DB 설정이 필요합니까?

승인하시면 즉시 Phase 1부터 순차적으로 구현을 시작하겠습니다.

---

**계획 작성 완료일**: 2026-01-06 23:31
**다음 단계**: 사용자 승인 대기 → Phase 2 (Implementation) 진행
