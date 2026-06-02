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
| **Frontend** | Next.js 15 (App Router), TypeScript, React 19 |
| **Styling** | Vanilla CSS (글래스모피즘, 다크 테마, 모바일 퍼스트) |
| **AI** | Google Gemini API (`google-genai` Java SDK) |
| **외부 API** | OpenWeatherMap (날씨), Geolocation API (위치) |
| **API 문서** | Swagger / SpringDoc OpenAPI |

---

## 📁 프로젝트 구조

```
Capstone/
├── backend/                    # Spring Boot REST API (Port 8080)
│   ├── build.gradle            # Gradle 빌드 설정
│   ├── gradlew                 # Gradle Wrapper
│   └── src/main/
│       ├── java/com/capstone/sajurecommender/
│       │   ├── config/         # CORS, WebClient 설정
│       │   ├── common/         # 공통 응답 DTO, 예외 처리
│       │   └── features/
│       │       ├── saju/       # 사주 계산 엔진 + 분석기
│       │       ├── weather/    # 날씨 서비스 (OpenWeatherMap)
│       │       ├── recommend/  # 추천 엔진 + 장소 데이터
│       │       └── ai/         # Gemini AI 서비스
│       └── resources/
│           └── application.yml # 설정 파일
│
├── frontend/                   # Next.js 모바일 웹 UI (Port 3000)
│   ├── package.json            # npm 의존성 정의
│   ├── app/                    # 페이지 라우트 (App Router)
│   │   ├── page.tsx            # 랜딩 페이지
│   │   ├── input/page.tsx      # 사주 입력
│   │   ├── result/page.tsx     # 분석 결과
│   │   ├── context/page.tsx    # 기분/상황 선택
│   │   └── recommendations/page.tsx # 추천 결과
│   ├── components/             # 공통 컴포넌트
│   ├── lib/api.ts              # API 통신 유틸리티
│   └── types/saju.ts           # TypeScript 타입 정의
│
├── .gitignore
└── README.md
```

---

## 🚀 초기 세팅 가이드

### 사전 요구사항

| 도구 | 최소 버전 | 확인 명령어 |
|------|----------|------------|
| **Java JDK** | 21+ | `java --version` |
| **Node.js** | 18+ | `node --version` |
| **npm** | 9+ | `npm --version` |
| **Git** | 2.x | `git --version` |

### Step 1. 레포지토리 클론

```bash
git clone https://github.com/j8evn/Capstone-sajuro.git
cd Capstone-sajuro
```

### Step 2. 백엔드 설정 및 실행

> **💡 Spring Boot, Gradle은 별도 설치가 필요 없습니다.**
> 프로젝트에 포함된 Gradle Wrapper(`./gradlew`)가 Spring Boot를 포함한 모든 의존성을 자동으로 다운로드합니다.
> **Java JDK 21만 설치되어 있으면 됩니다.**

```bash
# 백엔드 디렉토리 이동
cd backend

# Gradle 빌드 (Spring Boot + 모든 라이브러리 자동 다운로드, 최초 실행 시 수 분 소요)
./gradlew build -x test

# 서버 실행
./gradlew bootRun
```

> 서버가 `http://localhost:8080` 에서 시작됩니다.
> Swagger UI: http://localhost:8080/swagger-ui.html

#### 백엔드 주요 의존성 (Gradle이 자동 관리)

| 의존성 | 용도 |
|--------|------|
| `spring-boot-starter-web` | REST API 서버 |
| `spring-boot-starter-webflux` | WebClient (외부 API 호출) |
| `spring-boot-starter-validation` | 요청 유효성 검증 |
| `springdoc-openapi` | Swagger API 문서 자동 생성 |
| `google-genai` | Google Gemini AI SDK |
| `lombok` | 보일러플레이트 코드 자동 생성 |

### Step 3. 프론트엔드 설정 및 실행

```bash
# 새 터미널을 열고 프론트엔드 디렉토리로 이동
cd frontend

# npm 패키지 설치 (최초 1회 필수)
npm install

# 개발 서버 실행
npm run dev
```

> 앱이 `http://localhost:3000` 에서 시작됩니다.

#### 프론트엔드 주요 의존성

| 패키지 | 버전 | 용도 |
|--------|------|------|
| `next` | 16.2.7 | React 프레임워크 (App Router) |
| `react` | 19.2.4 | UI 라이브러리 |
| `react-dom` | 19.2.4 | React DOM 렌더링 |
| `typescript` | ^5 | 정적 타입 검사 |
| `eslint` | ^9 | 코드 린트 |

### Step 4. (선택) 환경변수 설정

```bash
# 백엔드 환경변수 (터미널에서 설정 후 ./gradlew bootRun)
export GEMINI_API_KEY="your-gemini-api-key"          # Gemini AI 활성화
export OPENWEATHER_API_KEY="your-openweather-key"    # 실시간 날씨 데이터
```

| 환경변수 | 기본값 | 설명 |
|----------|--------|------|
| `GEMINI_API_KEY` | `none` | Gemini AI API 키. 없으면 템플릿 기반 텍스트로 폴백 |
| `OPENWEATHER_API_KEY` | `mock` | OpenWeatherMap API 키. 없으면 목업 날씨 데이터 사용 |
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080` | 프론트엔드→백엔드 API 주소 |

> **API 키 없이도 모든 기능이 동작합니다.** 목업 데이터와 템플릿 텍스트로 폴백됩니다.

### Step 5. 동작 확인

1. 백엔드 실행 상태 확인: http://localhost:8080/swagger-ui.html
2. 프론트엔드 접속: http://localhost:3000
3. 사주 입력 → 분석 결과 → 기분 선택 → 추천 결과 순으로 테스트

---

## 📡 API 엔드포인트

| Method | Path | 설명 | 요청 본문 |
|--------|------|------|----------|
| `POST` | `/api/saju/calculate` | 사주팔자 계산 + 오행 분석 | `{ year, month, day, hour, calendarType, gender }` |
| `GET` | `/api/weather?lat=&lon=` | 현재 날씨 + 오행 매핑 | Query Params |
| `POST` | `/api/recommend` | 상황 인지 기반 맞춤 장소 추천 | `{ sajuInput, mood, lat, lon }` |

Swagger UI: http://localhost:8080/swagger-ui.html

### 요청 예시

```bash
# 사주 계산
curl -X POST http://localhost:8080/api/saju/calculate \
  -H "Content-Type: application/json" \
  -d '{"year":1998,"month":3,"day":15,"hour":14,"calendarType":"solar","gender":"female"}'

# 날씨 조회
curl "http://localhost:8080/api/weather?lat=37.5665&lon=126.978"

# 추천 요청
curl -X POST http://localhost:8080/api/recommend \
  -H "Content-Type: application/json" \
  -d '{"sajuInput":{"year":1998,"month":3,"day":15,"hour":14,"calendarType":"solar","gender":"female"},"mood":"happy","lat":37.5665,"lon":126.978}'
```

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
├── birthInfo               # 생년월일시, 달력 유형, 성별
├── fourPillars             # 사주팔자 (연주/월주/일주/시주)
│   └── 각 기둥: 천간/지지/오행/음양/띠
├── analysis                # 오행 분포, 용신, 성격 키워드
│   └── personalityDescription  # (AI) 성격 설명
├── marketingVariables      # 선호 색상/음식/활동/방위
└── dailyFortune            # 오늘의 운세 점수
    ├── description         # 템플릿 기반 운세
    └── aiDescription       # (AI) Gemini 생성 운세
```

### 추천 점수 산출 공식

```
총점 = (사주적합도 × 0.35) + (기분적합도 × 0.25) 
     + (날씨적합도 × 0.20) + (시간적합도 × 0.10)
     + (혼잡도점수 × 0.10)
```

---

## 🔧 개발 명령어 모음

### 백엔드 (`/backend`)

```bash
./gradlew bootRun          # 개발 서버 실행
./gradlew build            # 프로덕션 빌드 (JAR 생성)
./gradlew build -x test    # 테스트 제외 빌드
./gradlew test             # 테스트 실행
```

### 프론트엔드 (`/frontend`)

```bash
npm install                # 의존성 설치 (최초/변경 시)
npm run dev                # 개발 서버 실행 (Hot Reload)
npm run build              # 프로덕션 빌드
npm run start              # 프로덕션 서버 실행
npm run lint               # ESLint 코드 검사
```

---

## 👥 팀원

| 이름 | 역할 |
|------|------|
| 진주은 | 개발 |

## 📄 라이선스

이 프로젝트는 캡스톤 디자인 과제로 제작되었습니다.
