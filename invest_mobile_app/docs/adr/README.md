# Architecture Decision Records (ADR)

이 폴더는 프로젝트의 주요 아키텍처 결정사항을 기록합니다.

## ADR이란?
Architecture Decision Record(ADR)는 소프트웨어 개발 중 내린 중요한 아키텍처 결정을 문서화하는 방법입니다.

## 목적
- 왜 특정 기술이나 패턴을 선택했는지 기록
- 새로운 팀원의 빠른 온보딩
- 미래의 자신과 팀원을 위한 컨텍스트 제공
- 의사결정 과정의 투명성 확보

## ADR 작성 시기
다음과 같은 결정을 내릴 때 ADR을 작성합니다:
- 아키텍처 패턴 선택
- 상태 관리 솔루션 선택
- 주요 라이브러리나 프레임워크 선택
- 폴더 구조 변경
- 데이터베이스 선택
- API 설계 원칙
- 보안 정책
- 테스팅 전략

## ADR 구조
각 ADR은 다음 구조를 따릅니다:

```markdown
# ADR XXXX: [결정 제목]

## Status
[Proposed | Accepted | Deprecated | Superseded]

## Date
YYYY-MM-DD

## Context
[어떤 상황에서 이 결정을 내리게 되었는가?]

## Decision
[무엇을 결정했는가?]

## Consequences
[이 결정의 긍정적/부정적 결과는?]

### Positive
- 장점 1
- 장점 2

### Negative
- 단점 1
- 단점 2

## Alternatives Considered
[고려했던 다른 옵션들과 거부 이유]

## References
[관련 문서, 링크, 논의]
```

## 기존 ADR 목록

| 번호 | 제목 | 상태 | 날짜 |
|------|------|------|------|
| [0001](0001-adopt-clean-architecture.md) | Clean Architecture 도입 | Accepted | 2026-01-20 |
| [0002](0002-choose-feature-first-structure.md) | Feature-First 프로젝트 구조 채택 | Accepted | 2026-01-20 |
| [0003](0003-state-management-with-riverpod.md) | Riverpod를 상태 관리 솔루션으로 선택 | Accepted | 2026-01-20 |

## 새 ADR 추가 방법

1. 다음 번호의 파일 생성: `XXXX-descriptive-title.md`
2. 위의 템플릿 사용
3. 이 README의 목록에 추가
4. PR 생성 및 팀 리뷰

## 규칙
- ADR 번호는 4자리 숫자 (0001, 0002, ...)
- 파일명은 kebab-case 사용
- 한번 Accepted된 ADR은 수정하지 않음
- 결정을 변경하려면 새 ADR을 작성하고 기존 ADR을 Superseded로 표시

## 참고 자료
- [ADR GitHub Organization](https://adr.github.io/)
- [Documenting Architecture Decisions by Michael Nygard](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
