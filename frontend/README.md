# 사주로 — Frontend

Next.js 15 (App Router) 기반 모바일 퍼스트 웹 UI

## 기술 스택

- **Framework**: Next.js 15 (TypeScript, App Router)
- **Styling**: Vanilla CSS (글래스모피즘 다크 테마)
- **API 통신**: Fetch API → Spring Boot 백엔드 (`localhost:8080`)

## 실행

```bash
npm install
npm run dev
```

http://localhost:3000 에서 확인

## 페이지 구조

| 경로 | 설명 |
|------|------|
| `/` | 랜딩 페이지 (온보딩) |
| `/input` | 사주 입력 폼 (생년월일시) |
| `/result` | 사주 분석 결과 (명반, 오행 차트, 운세) |
| `/context` | 기분/상황 선택 (날씨 카드 + 이모지 기분) |
| `/recommendations` | 맞춤 추천 결과 (장소 카드 + 점수 분석) |

## 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080` | 백엔드 API 주소 |
