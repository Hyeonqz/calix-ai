# Example Auth Feature

이 폴더는 Clean Architecture의 Feature-First 구조를 보여주는 예제입니다.

## 구조

### 📱 Presentation Layer
- **pages/**: 화면 위젯
- **widgets/**: 재사용 가능한 UI 컴포넌트
- **state/**: 상태 관리 (Provider/Riverpod/Bloc)

### 💼 Domain Layer (핵심 비즈니스 로직)
- **entities/**: 순수한 비즈니스 모델
- **repositories/**: Repository 인터페이스 정의
- **usecases/**: 단일 책임을 가진 비즈니스 로직

### 📦 Data Layer
- **models/**: DTO (Data Transfer Objects)
- **repositories/**: Repository 구현체
- **datasources/**:
  - `remote/`: API 통신
  - `local/`: 로컬 저장소 (SharedPreferences, Hive 등)

## 의존성 규칙

```
Presentation → Domain ← Data
```

- Presentation과 Data는 Domain에 의존
- Domain은 어디에도 의존하지 않음 (순수한 Dart 코드)
- Data와 Presentation은 서로를 알지 못함

## 실제 구현 시

1. **Domain 먼저**: Entity, Repository Interface, UseCase 정의
2. **Data 구현**: Repository 구현, DataSource 작성
3. **Presentation 구현**: UI 및 상태 관리
4. **DI 설정**: 의존성 주입 구성
5. **테스트 작성**: 각 레이어별 테스트

## 주의사항

- 이 폴더는 **예제**입니다. 실제 구현 시 `example_` 접두사를 제거하세요.
- 각 feature는 독립적으로 동작할 수 있어야 합니다.
- 여러 feature에서 공통으로 사용하는 코드는 `shared/` 또는 `core/`에 위치시키세요.
