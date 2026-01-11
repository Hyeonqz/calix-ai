# Claude Code Configuration - Frontend Service

## Project Context
- **Tech Stack**: React 19, Next.js 16 (App Router), TypeScript, TailwindCSS
- **Domain**: 금융 투자 서비스 프론트엔드
- **Architecture**: Feature-based structure

## Development Workflow

### Phase 1: Planning (필수)
모든 작업 요청은 **즉시 실행하지 않고** 다음 절차를 따릅니다:

1. 요청사항 분석 후 `.claude/plans/YYYYMMDD_HHmmss_[task-name].md` 생성
2. 실행 계획 내용:
    - 작업 목표 및 범위
    - 영향받는 컴포넌트 목록
    - 구현 단계별 상세 계획
    - 예상 리스크 및 대응 방안
    - 테스트 전략
3. 계획 검토 후 승인 시에만 Phase 2 진행

### Phase 2: Implementation
승인된 계획에 따라 구현을 진행합니다.

### Phase 3: Documentation (필수)
코드 변경사항 발생 시 `.claude/docs/changes/YYYYMMDD_[task-name].md` 생성:
```markdown
# Change Log: [Task Name]

## As-Is (변경 전)
- 현재 구조 및 로직 설명
- 문제점 또는 개선 필요 사항
- 코드 스니펫 (핵심 부분)

## To-Be (변경 후)
- 변경된 구조 및 로직 설명
- 개선 효과
- 코드 스니펫 (핵심 부분)

## Impact Analysis
- 영향받는 API 엔드포인트
- 데이터베이스 스키마 변경 여부
- 호환성 이슈
- 롤백 계획

## Testing
- 단위 테스트 추가/수정 내역
- 통합 테스트 시나리오
```

### Phase 2: Implementation
승인된 계획에 따라 구현을 진행합니다.

### Phase 3: Documentation (필수)
컴포넌트/페이지 변경 시 `.claude/docs/changes/YYYYMMDD_[task-name].md` 생성:
```markdown
# Change Log: [Task Name]

## As-Is (변경 전)
- 현재 컴포넌트 구조
- 상태 관리 방식
- UI/UX 플로우

## To-Be (변경 후)
- 변경된 컴포넌트 구조
- 개선된 사용자 경험
- 성능 최적화 내역

## Impact Analysis
- 영향받는 페이지/컴포넌트
- API 호출 변경사항
- 번들 사이즈 변화

## Testing
- 컴포넌트 테스트
- E2E 시나리오
```

## Code Standards

### Component Structure
```
src/
  features/
    investment/
      components/
      hooks/
      types/
      api/
```

### Naming Conventions
- 컴포넌트: PascalCase
- Hooks: use + PascalCase
- Utils: camelCase

### State Management
- Server State: React Query
- Client State: Zustand
- Form State: React Hook Form

## Skills & Agents
- `/skills/nextjs-optimization.md`: Next.js 성능 최적화
- `/skills/react-patterns.md`: React 디자인 패턴
- `/agents/component-generator.md`: 컴포넌트 자동 생성