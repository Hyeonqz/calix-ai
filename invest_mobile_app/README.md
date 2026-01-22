# Invest Mobile App

투자 정보 제공 모바일 애플리케이션

## 프로젝트 개요

이 프로젝트는 Clean Architecture와 Feature-First 구조를 기반으로 한 Flutter 모바일 애플리케이션입니다.

## 아키텍처

### Clean Architecture + Feature-First
```
lib/
├── core/                    # 앱 전체 공통 기능
│   ├── constants/          # 상수
│   ├── theme/              # 테마
│   ├── utils/              # 유틸리티
│   ├── di/                 # 의존성 주입
│   ├── error/              # 에러 처리
│   └── network/            # 네트워크 설정
├── features/               # 기능별 모듈
│   └── [feature]/
│       ├── presentation/   # UI Layer
│       ├── domain/         # Business Logic
│       └── data/           # Data Layer
├── shared/                 # 공유 컴포넌트
│   ├── widgets/
│   └── models/
└── main.dart
```

자세한 아키텍처 결정사항은 [ADR 문서](.claude/docs/adr/)를 참조하세요.

## 기술 스택

### Core
- **Flutter SDK**: ^3.9.2
- **Dart**: ^3.9.2

### 상태 관리
- **Riverpod**: ^2.6.1

### 네트워킹
- **Dio**: ^5.7.0

### 함수형 프로그래밍
- **Dartz**: ^0.10.1

### 코드 생성
- **Freezed**: ^2.5.7
- **JSON Serializable**: ^6.8.0

### 라우팅
- **Go Router**: ^14.6.2

### 로컬 저장소
- **Shared Preferences**: ^2.3.3

### 유틸리티
- **Equatable**: ^2.0.7

### 테스팅
- **Mockito**: ^5.4.4
- **Mocktail**: ^1.0.4

## 빠른 시작 (Quick Start)

처음 프로젝트를 실행하는 경우:

```bash
# 1. 의존성 설치
flutter pub get

# 2. 웹 빌드
flutter build web --release

# 3. 로컬 서버 실행
cd build/web && python3 -m http.server 8000

# 4. 브라우저에서 http://localhost:8000 접속
```

또는 개발 모드로 바로 실행:

```bash
flutter run -d chrome
# 또는
flutter run -d macos
```

## 시작하기

### 필수 요구사항
- Flutter SDK 3.9.2 이상
- Dart 3.9.2 이상

### 설치

1. 저장소 클론
```bash
git clone [repository-url]
cd invest_mobile_app
```

2. 의존성 설치
```bash
flutter pub get
```

3. 코드 생성 (필요시)
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

4. 앱 실행

**개발 모드 (Hot Reload 지원):**
```bash
# Chrome에서 실행 (개발 중 권장)
flutter run -d chrome

# macOS 데스크톱 앱으로 실행
flutter run -d macos

# 실행 중 Hot Reload: 터미널에서 'r' 입력
# 실행 중 Hot Restart: 터미널에서 'R' 입력
```

**프로덕션 빌드 및 미리보기:**
```bash
# 1. 웹 빌드 (최적화된 프로덕션 빌드)
flutter build web --release

# 2. 로컬 서버 실행
cd build/web
python3 -m http.server 8000

# 3. 브라우저에서 접속
# http://localhost:8000
```

## 개발 가이드

### 코딩 컨벤션
- 파일명: `snake_case`
- 클래스명: `PascalCase`
- 변수/함수명: `camelCase`
- Private 멤버: `_leadingUnderscore`

자세한 내용은 [CLAUDE.md](./claude/CLAUDE.md)를 참조하세요.

### 개발 워크플로우

#### 1. 코드 수정 후 Hot Reload
```bash
# 앱이 실행 중일 때 (flutter run -d chrome)
# 코드 수정 → 저장 → 터미널에서 'r' 입력
# 변경사항이 즉시 반영됩니다
```

#### 2. 상태 초기화가 필요할 때
```bash
# 터미널에서 'R' 입력 (Hot Restart)
# 앱이 완전히 재시작되며 모든 상태가 초기화됩니다
```

#### 3. 빌드 후 배포 미리보기
```bash
# 1. 프로덕션 빌드
flutter build web --release

# 2. 로컬 서버로 확인
cd build/web && python3 -m http.server 8000

# 3. 브라우저에서 http://localhost:8000 접속

# 4. 서버 종료 (확인 후)
pkill -f "python3 -m http.server"
```

#### 4. 코드 생성 (freezed, json_serializable 사용 시)
```bash
# watch 모드: 파일 변경 시 자동 생성
flutter pub run build_runner watch --delete-conflicting-outputs

# 일회성 생성
flutter pub run build_runner build --delete-conflicting-outputs
```

#### 5. 디바이스 관리
```bash
# 사용 가능한 디바이스 확인
flutter devices

# 특정 디바이스로 실행
flutter run -d <device_id>

# 여러 디바이스에서 동시 실행
flutter run -d chrome & flutter run -d macos
```

#### 6. 서버 관리
```bash
# 실행 중인 서버 확인
lsof -i :8000

# 서버 종료
pkill -f "python3 -m http.server"

# 특정 포트로 서버 실행
python3 -m http.server 3000
```

### 새 Feature 추가하기

1. Feature 폴더 생성
```bash
mkdir -p lib/features/my_feature/{presentation,domain,data}
```

2. Domain Layer 먼저 구현
   - entities/
   - repositories/ (interface)
   - usecases/

3. Data Layer 구현
   - models/
   - repositories/ (implementation)
   - datasources/

4. Presentation Layer 구현
   - pages/
   - widgets/
   - state/

5. DI 설정 (Riverpod Providers)

### 테스트 실행

```bash
# 모든 테스트 실행
flutter test

# 특정 테스트 파일
flutter test test/features/auth/domain/usecases/sign_in_test.dart

# 커버리지 포함
flutter test --coverage
```

### 코드 분석

```bash
# Lint 검사
flutter analyze

# 포맷팅
dart format lib/
```

## 문제 해결

### Impeller 관련 에러
Chrome에서 실행 시 `ShaderCompilerException: The impellerc utility is missing` 에러가 발생하는 경우:

**해결 방법 1: HTML 렌더러 사용**
```bash
flutter run -d chrome --web-renderer html
```

**해결 방법 2: macOS 데스크톱 앱으로 실행**
```bash
flutter run -d macos
```

**해결 방법 3: Flutter 캐시 재설치** (네트워크 연결 필요)
```bash
# Flutter 캐시 삭제
rm -rf /opt/homebrew/share/flutter/bin/cache
# 또는: flutter clean

# 웹 아티팩트 재다운로드
flutter precache --web

# 프로젝트 의존성 재설치
flutter pub get

# Chrome에서 실행
flutter run -d chrome
```

### Chrome 크래시 문제
Chrome에서 Dart 컴파일러가 크래시하는 경우:

**해결 방법: 프로덕션 빌드 사용**
```bash
# 1. 웹 빌드
flutter build web --release

# 2. 로컬 서버 실행
cd build/web && python3 -m http.server 8000

# 3. http://localhost:8000 접속
```

### 기타 문제

**캐시 정리**
```bash
flutter clean
flutter pub get
```

**디바이스 확인**
```bash
flutter devices
```

**의존성 업데이트**
```bash
# 모든 패키지 최신 버전 확인
flutter pub outdated

# 의존성 업그레이드
flutter pub upgrade
```

**포트 충돌 해결**
```bash
# 포트 8000을 사용 중인 프로세스 확인
lsof -i :8000

# 프로세스 종료
kill -9 <PID>

# 또는 다른 포트 사용
python3 -m http.server 3000
```

**빌드 오류 발생 시**
```bash
# 1. 캐시 정리
flutter clean

# 2. pub cache 정리
flutter pub cache repair

# 3. 의존성 재설치
flutter pub get

# 4. 코드 생성 재실행 (필요시)
flutter pub run build_runner clean
flutter pub run build_runner build --delete-conflicting-outputs
```

## 프로젝트 구조 예제

`lib/features/example_auth/` 폴더에 Clean Architecture 예제가 있습니다.
실제 구현 시 참고하세요.

## 문서

- [CLAUDE.md](./claude/CLAUDE.md) - 개발 가이드 및 컨벤션
- [ADR](.claude/docs/adr/) - 아키텍처 결정 기록
- [Example Feature](./lib/features/example_auth/README.md) - 예제 구조

## Git 커밋 컨벤션

```
<type>(<scope>): <subject>

feat: 새로운 기능
fix: 버그 수정
refactor: 리팩토링
style: 코드 포맷팅
test: 테스트 추가/수정
docs: 문서 수정
chore: 빌드, 패키지 설정 등
```

예시:
```
feat(auth): implement login with email and password
```

## 라이선스

[라이선스 타입]

## 팀

[팀 정보]
