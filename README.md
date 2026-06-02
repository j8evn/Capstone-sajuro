# 🔮 사주로 (Sajuro)

> 사주 표준화 데이터 기반 초개인화 상황 인지(Context-Aware) 추천 시스템

사용자의 사주 데이터와 실시간 상황 데이터(기분, 날씨, 시간)를 결합하여 최적의 장소와 아이템을 추천하는 모바일 웹 프로토타입입니다.

---

## 📌 프로젝트 개요

사주명리학은 오랜 시간 축적된 개인 맞춤형 데이터임에도 불구하고, 데이터의 비정형성과 해석의 주관성 때문에 현대적 마케팅 기술과 결합하기 어려웠습니다.

**사주로**는 사주 데이터를 공학적으로 모델링하여 표준화된 마케팅 변수로 변환하고, 실시간 상황 인지를 결합한 추천 시스템을 제공합니다.

### 핵심 기능

| 기능 | 설명 |
|------|------|
| **사주 자동 계산** | 생년월일시 입력 → 사주팔자(60갑자) 자동 산출 |
| **오행 분석** | 오행 분포, 음양 균형, 용신 분석 및 마케팅 변수 변환 |
| **상황 인지 매칭** | 사주(35%) + 기분(25%) + 날씨(20%) + 시간(10%) + 혼잡도(10%) 가중치 매칭 |
| **AI 해석** | Google Gemini API 기반 자연어 사주 해석 및 추천 이유 생성 |
| **맞춤 추천** | 오행 속성이 태깅된 장소/메뉴를 개인화 점수로 랭킹 |

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| **Backend** | Java 21, Spring Boot 3.4.2, Gradle 8.12 |
| **Frontend** | Next.js 15 (App Router), TypeScript, Vanilla CSS |
| **AI** | Google Gemini API (`google-genai` SDK) |
| **외부 API** | OpenWeatherMap (날씨), Geolocation API (위치) |
| **Design** | 모바일 퍼스트, 글래스모피즘, 다크 테마 |

---

## 📁 프로젝트 구조

```
Capstone/
├── backend/                    # Spring Boot REST API
│   └── src/main/java/com/capstone/sajurecommender/
│       ├── config/             # CORS, WebClient 설정
│       ├── common/             # 공통 응답 DTO, 예외 처리
│       └── features/
│           ├── saju/           # 사주 계산 엔진 + 분석기
│           │   ├── domain/     # SajuConstants, Pillar, FourPillars
│           │   ├── service/    # SajuCalculator, SajuAnalyzer
│           │   ├── dto/        # SajuRequest, SajuProfileResponse
│           │   └── controller/ # SajuController
│           ├── weather/        # 날씨 서비스 (OpenWeatherMap)
│           ├── recommend/      # 추천 엔진 + 장소 데이터
│           └── ai/             # Gemini AI 서비스
├── frontend/                   # Next.js 모바일 웹 UI
│   ├── app/                    # 페이지 라우트
│   │   ├── page.tsx            # 랜딩 페이지
│   │   ├── input/              # 사주 입력
│   │   ├── result/             # 분석 결과
│   │   ├── context/            # 기분/상황 선택
│   │   └── recommendations/    # 추천 결과
│   ├── components/             # 공통 컴포넌트
│   ├── lib/                    # API 유틸리티
│   └── types/                  # TypeScript 타입 정의
└── README.md
```

---

## 🚀 실행 방법

### 사전 준비

- Java 21+
- Node.js 18+
- (선택) Gemini API Key: [Google AI Studio](https://aistudio.google.com/)에서 발급

### 1. 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

서버가 `http://localhost:8080`에서 실행됩니다.

### 2. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

앱이 `http://localhost:3000`에서 실행됩니다.

### 3. (선택) Gemini AI 활성화

```bash
export GEMINI_API_KEY="your-api-key-here"
cd backend && ./gradlew bootRun
```

> API 키 없이도 동작합니다 — 템플릿 기반 텍스트로 폴백됩니다.

---

## 📡 API 엔드포인트

| Method | Path | 설명 |
|--------|------|------|
| `POST` | `/api/saju/calculate` | 사주팔자 계산 + 오행 분석 + AI 해석 |
| `GET` | `/api/weather?lat=&lon=` | 현재 날씨 + 오행 매핑 |
| `POST` | `/api/recommend` | 상황 인지 기반 맞춤 장소 추천 |

Swagger UI: http://localhost:8080/swagger-ui.html

---

## 🔄 사용자 플로우

```
[랜딩] → [사주 입력] → [분석 결과] → [기분/상황 선택] → [맞춤 추천]
  홈       생년월일시     사주 명반        날씨 + 기분       장소 + 메뉴
                         오행 분포                          점수 분석
                         운세 점수                          추천 이유
```

---

## 📊 데이터 표준화 스키마

사주 데이터는 다음과 같은 구조로 표준화됩니다:

```
SajuProfile
├── fourPillars          # 사주팔자 (천간/지지/오행/음양)
├── analysis             # 오행 분포, 용신, 성격 키워드
├── marketingVariables   # 선호 색상/음식/활동/방위
└── dailyFortune         # 오늘의 운세 점수 + AI 해석
```

### 추천 점수 산출 공식

```
총점 = (사주적합도 × 0.35) + (기분적합도 × 0.25) 
     + (날씨적합도 × 0.20) + (시간적합도 × 0.10)
     + (혼잡도점수 × 0.10)
```
