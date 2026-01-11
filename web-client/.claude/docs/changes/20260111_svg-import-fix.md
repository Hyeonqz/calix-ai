# Change Log: SVG Import 에러 수정

**날짜**: 2026-01-11
**작업자**: Claude Sonnet 4.5
**작업 유형**: Bug Fix

---

## As-Is (변경 전)

### 문제 상황
- localhost:3000 접속 시 "Element type is invalid" 에러 발생
- `/dashboard` 페이지 프리렌더링 실패
- 빌드 과정에서 에러 발생

### 에러 메시지
```
Error occurred prerendering page "/dashboard"
Error: Element type is invalid: expected a string (for built-in components)
or a class/function (for composite components) but got: object.
```

### 원인 분석
1. `src/icons/index.tsx`에서 SVG 파일을 React 컴포넌트로 import
2. Next.js 기본 설정에서는 SVG를 URL 문자열로 처리
3. @svgr/webpack이 설치되지 않아 SVG를 컴포넌트로 변환 불가
4. Next.js 16부터 Turbopack이 기본값이지만, webpack 설정 필요

---

## To-Be (변경 후)

### 해결 방법
1. `@svgr/webpack` 패키지 설치
2. `next.config.ts`에 webpack 설정 추가
3. TypeScript SVG 모듈 타입 정의 추가
4. package.json 스크립트를 webpack 모드로 변경

### 변경된 파일

#### 1. package.json
```json
{
  "scripts": {
    "dev": "next dev --webpack",    // 변경됨
    "build": "next build --webpack", // 변경됨
    "start": "next start",
    "lint": "eslint"
  },
  "devDependencies": {
    "@svgr/webpack": "^8.1.0",      // 추가됨
    // ... 기타 의존성
  }
}
```

**이유**: Next.js 16의 기본 Turbopack 대신 webpack 모드 사용

#### 2. next.config.ts
```typescript
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  webpack(config) {
    // SVG 파일을 React 컴포넌트로 import할 수 있도록 설정
    config.module.rules.push({
      test: /\.svg$/,
      use: ["@svgr/webpack"],
    });

    return config;
  },
};

export default nextConfig;
```

**변경 내용**:
- webpack 설정 함수 추가
- SVG 파일에 대한 @svgr/webpack 로더 설정

#### 3. src/types/svg.d.ts (신규 생성)
```typescript
declare module "*.svg" {
  import React from "react";
  const SVG: React.FC<React.SVGProps<SVGSVGElement>>;
  export default SVG;
}
```

**목적**: TypeScript가 SVG import를 인식하도록 타입 정의

---

## Impact Analysis

### 영향받는 컴포넌트
- ✅ `src/components/Sidebar/AppSidebar.tsx` - 아이콘 정상 렌더링
- ✅ `src/components/Header/AppHeader.tsx` - 아이콘 정상 렌더링
- ✅ `src/icons/index.tsx` - SVG import 정상 작동

### 빌드 및 런타임
- ✅ 빌드 성공 (webpack 모드)
- ✅ 개발 서버 정상 실행
- ✅ 대시보드 페이지 렌더링 성공
- ✅ 모든 아이콘 표시 정상

### 성능
- 빌드 시간: 4.6초 (webpack 모드)
- 개발 서버 시작: 936ms
- 페이지 컴파일: 3.8초 (최초 접속)

### Turbopack vs Webpack
- **선택**: Webpack 모드 사용
- **이유**: @svgr/webpack과 호환성 보장
- **Trade-off**: Turbopack보다 빌드 속도 느림 (허용 가능한 수준)

---

## Testing

### 빌드 테스트
```bash
npm run build -- --webpack
```
- ✅ TypeScript 컴파일 성공
- ✅ 3개 페이지 정적 생성 성공 (`/`, `/_not-found`, `/dashboard`)
- ✅ 에러 없음

### 런타임 테스트
```bash
npm run dev
```
- ✅ 개발 서버 정상 시작
- ✅ http://localhost:3000 접속 → `/dashboard`로 리다이렉트
- ✅ 대시보드 페이지 렌더링
- ✅ 사이드바 아이콘 표시
- ✅ 헤더 아이콘 표시
- ✅ 다크 모드 토글 작동

### 브라우저 테스트
- ✅ HTML 정상 렌더링
- ✅ SVG 아이콘 컴포넌트로 변환됨
- ✅ JavaScript 에러 없음

---

## 알려진 제약사항

### 1. Turbopack 미사용
- Next.js 16의 기본 번들러인 Turbopack을 사용하지 않음
- webpack 모드로 실행하여 빌드 속도가 다소 느릴 수 있음
- 향후 Turbopack에서 SVG 컴포넌트 지원 시 마이그레이션 고려

### 2. 대안 (미적용)
다음 대안들을 검토했으나 현재 구조를 유지하기 위해 채택하지 않음:
- SVG를 inline JSX 컴포넌트로 변환
- react-icons 같은 아이콘 라이브러리 사용
- next-plugin-svgr 사용 (webpack 설정과 동일한 결과)

---

## 다음 단계

### 권장 사항
1. ✅ **즉시**: 현재 webpack 모드로 개발 진행
2. **향후**: Turbopack SVG 지원 확인 및 마이그레이션 검토
3. **선택적**: 자주 사용하는 아이콘은 별도 라이브러리로 분리 고려

### 성능 최적화 (필요시)
- SVG 스프라이트 생성
- 사용하지 않는 아이콘 제거
- Tree-shaking 최적화

---

## 참고 링크

- [@svgr/webpack 공식 문서](https://react-svgr.com/docs/webpack/)
- [Next.js 16 Turbopack 문서](https://nextjs.org/docs/app/api-reference/next-config-js/turbopack)
- [계획 문서](.claude/plans/20260111_000000_svg-import-fix.md)

---

## 요약

**문제**: SVG import 에러로 대시보드 페이지 렌더링 실패
**해결**: @svgr/webpack 설치 및 webpack 모드로 전환
**결과**: 모든 페이지 정상 작동, 빌드 및 개발 서버 성공
**소요 시간**: 약 10분
