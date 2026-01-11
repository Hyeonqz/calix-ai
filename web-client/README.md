# Financial Admin Dashboard

금융 및 증권 백오피스 관리 시스템 프론트엔드

## 기술 스택

- **프레임워크**: Next.js 16.1.1 (App Router)
- **UI 라이브러리**: React 19.2.3
- **스타일링**: Tailwind CSS 4
- **템플릿**: TailAdmin Free Next.js Admin Dashboard
- **차트**: ApexCharts
- **언어**: TypeScript

## 프로젝트 구조

```
web-client/
├── app/
│   ├── (dashboard)/          # 대시보드 레이아웃 그룹
│   │   ├── layout.tsx        # Sidebar + Header 레이아웃
│   │   └── page.tsx          # 메인 대시보드
│   ├── layout.tsx            # 루트 레이아웃
│   └── globals.css
├── src/
│   ├── components/           # 공통 컴포넌트
│   │   ├── Sidebar/
│   │   ├── Header/
│   │   ├── Charts/
│   │   ├── common/
│   │   └── ui/
│   ├── context/              # React Context
│   │   ├── ThemeContext.tsx
│   │   └── SidebarContext.tsx
│   ├── features/             # Feature-based 구조
│   │   ├── investment/
│   │   ├── trading/
│   │   └── portfolio/
│   ├── icons/                # SVG 아이콘
│   └── types/
└── public/
    └── images/
```

## 시작하기

### 설치

```bash
npm install
```

### 개발 서버 실행

```bash
npm run dev
```

브라우저에서 [http://localhost:3000](http://localhost:3000) 열기

### 빌드

```bash
npm run build
```

### 프로덕션 서버

```bash
npm start
```

## 주요 기능

### 대시보드
- 총 자산, 포트폴리오, 주문, 고객 통계
- 포트폴리오 성과 차트
- 최근 활동 피드
- 거래 내역 테이블

### 레이아웃
- 반응형 Sidebar (접기/펼치기)
- 고정 Header (검색, 알림, 사용자 메뉴)
- 다크 모드 지원
- 모바일 대응

### 메뉴 구조
```
Main Menu:
- Dashboard
- Investment (Portfolio, Holdings, Performance)
- Trading (Orders, History, Positions)
- Clients (All Clients, KYC, Documents)

Others:
- Analytics (Reports, Charts)
- Settings
```

## Feature-based 구조

각 도메인별로 독립적인 모듈 구조:

```
features/
  investment/
    ├── components/    # 투자 관련 UI 컴포넌트
    ├── hooks/         # 커스텀 훅
    ├── types/         # TypeScript 타입
    ├── api/           # API 호출 함수
    └── README.md
```

## 개발 가이드

### 컴포넌트 명명 규칙
- **컴포넌트**: PascalCase (예: `AppSidebar.tsx`)
- **훅**: use + PascalCase (예: `useTheme`, `useSidebar`)
- **유틸**: camelCase

### 경로 alias
- `@/*`: src 디렉토리
- `@/app/*`: app 디렉토리

### 상태 관리
- **Server State**: React Query (예정)
- **Client State**: Context API + Zustand (예정)
- **Theme**: ThemeContext
- **Sidebar**: SidebarContext

## 스크립트

```bash
# 개발 서버
npm run dev

# 프로덕션 빌드
npm run build

# 프로덕션 서버
npm start

# Linting
npm run lint
```

## 다음 작업

### Phase 3: API 통합
- [ ] 백엔드 API 연동
- [ ] React Query 설정
- [ ] 실제 금융 데이터 통합

### Phase 4: 페이지 완성
- [ ] Investment 페이지
- [ ] Trading 페이지
- [ ] Clients 페이지
- [ ] Analytics 페이지

### Phase 5: 인증
- [ ] 로그인/회원가입
- [ ] JWT 또는 세션 기반 인증
- [ ] 보호된 라우트

### Phase 6: 최적화
- [ ] Dynamic import
- [ ] 이미지 최적화
- [ ] Code splitting

## 참고 문서

- [변경 로그](./.claude/docs/changes/20260108_tailadmin-integration.md)
- [계획 문서](./.claude/plans/20260108_175500_tailadmin-template-integration.md)
- [CLAUDE.md](./.claude/CLAUDE.md)

## 크레딧

- **템플릿**: [TailAdmin](https://github.com/TailAdmin/free-nextjs-admin-dashboard)
- **라이선스**: MIT
