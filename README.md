
프론트엔드 README.md : https://github.com/youngwan2/FN_mincho


# 🌿 사용자 증상 기반 약초 추천 커뮤니티, 민초
## 📌 프로젝트 개요
민간 약초에 대한 정보를 공유하고, 사용자 증상을 기반으로 알맞은 약초를 추천하는 서비스를 중심으로 사용자들이 직접 약초 관련 경험을 나눌 수 있는 커뮤니티 웹사이트입니다. 
<br><br>    
![image](https://github.com/user-attachments/assets/cd5f5bfd-f975-4628-8003-4c0e08ade7cf)
<br><br>    
## 🎯 프로젝트 목적과 방향성
#### **[목적]**  
- 방대한 약초 정보를 보다 체계적으로 정리하고, 사용자들이 신뢰할 수 있는 정보를 주고받을 수 있는 공간을 제공하는 것이 목표입니다.
- 기존 약초 관련 사이트는 정보가 파편화되어 찾아보기 힘들고, 방대한 정보를 사용자 친화적으로 제공하는 곳을 찾아보기 힘들었습니다. 따라서 사용자에게 맞춤형 약초정보와 사용자 활동 로그를 기반의 서비스를 제공하여 보다 사용자 친화적인 서비스를 제공하는 사이트를 만들고자 합니다.
#### **[방향성]**
현재는 RAG 기반의 챗 형태로 서비스를 MVP로 제공하지만, 추후 다양한 AI 기술과 추천시스템을 결합하고, 이미지 기반 검색, GIS 기반의 근처 약초 찾아가기 서비스 등을 추가해나가며 확장해 나갈 생각입니다.
<br><br>    
## 📅 개발 기간/유지보수
- **개발 기간:** 2025년 3월 13일 ~ 2025년 5월 30일
- **비고**: 명시된 기간에 비해실질적인 개발 기간이 짧아 더 연장 될 수 있습니다.
- **유지보수:** 정식 오픈 후 지속적인 업데이트 예정
 <br><br>
## 📃 문서
- [REST API <링크>](https://documenter.getpostman.com/view/23804313/2sAYX2Njky)
- [배포 히스토리 <링크>](https://youngwan2.notion.site/1e568acd779b80ef9cb7f760bc8f6e60)
- [트러블슈팅 <링크>](https://youngwan2.notion.site/1f468acd779b8009a829e956086d6342)
<br><br>
## 🔥 배포
- **데모**: https://www.minchoherb.com
<br><br>
## 시스템 아키텍처
![민초 아키텍처](https://github.com/user-attachments/assets/3c6aaa37-1bee-4035-a3b1-e3a40738d59e)
<br><br>
## ⚙ 기능
### 인증
#### **회원가입 및 로그인 (JWT 인증)**
  - `accessToken`과 `refreshToken`을 활용한 토큰 탈취를 고려한 인증 구현
  - refresh 토큰은 데이터베이스에 저장하고, 데이터베이스에 존재하지 않는 refresh 토큰이라면 인증이 거부되도록 구현
  - 만료 시 백엔드에서 설정한 jwt filter 로 부터 401 에러를 반환하고, 프론트의 axios 인터셉터에서 응답을 받은 후 refresh 토큰을 사용하여 access 토큰을 자동 재발급토록 구현
  - 일반적인 로컬 로그인뿐만 아니라 구글 소셜로그인의 경우에도 백엔드에서 모든 oauth2 인증을 진행하고, 인증 성공 시 access 와 refresh 토큰을 발급하게 하여 동일하게 처리하도록 구현


### 시스템
#### **SSE 기반 알림 기능**
  - 댓글 알림, 공지사항 알림이 존재하면 해당 알림을 실시간으로 확인할 수 있도록 구현.
    - 다만 현재 방식은 사용자가 오프라인인 경우에는 확인받을 수 없다는 제한점이 있어서 향후 PWA 적용과 FCM을 적용하여 모바일 상에서도 알림을 받을 수 있도록 개선할 예정
  - 알림 내역 관리를 위해 별도 알림 UI 추가(리액트 쿼리의 뮤테이션을 적용하여 변경사항 즉시 반영)
    - 별도로 사용자 경험 고려하여 useInfinitiQuery 기반의 무한 스크롤 적용
    ![image](https://github.com/user-attachments/assets/58331e6a-ba91-4d20-be74-c4ab8ee4561a)

#### **커뮤니티 기능과 S3 Presigned 이미지 업로드**
  - **커뮤니티(만남의장)**: tiptap 기반으로 제작된 커뮤니티 게시판
    - **이미지 업로드 기능**: 객체 기반의 스토로지 서비스인 **AWS S3**의 기능 중 하나인 **presigned URL**(파일에 대한 임시 접근 권한을 활성화하는 보안 URL)을 적용하여, 업로드와 동시에 S3에 이미지를 업로드.
    ![post](https://github.com/user-attachments/assets/c03cc2bb-a8c3-46a6-ba78-7da91377002d)
<br></br>
  - **커뮤니티(민초Q&A)**: tiptap 기반으로 제작된 커뮤니티 Q&A 게시판으로 질문 작성, 답변 작성, 채택, 질문 태깅, 시기별 필터링 등의 기본적인 전용 기능 제공
    - 만남의장 게시판과는 달리 presigned URL 을 적용하지 않고, 프론트엔드에서는 createObjectURL 을 사용하여 미리보기를 구현하고, 실제 질문이나 답변 업로드 시 동기적으로 게시글 등록 -> 이미지 등록이 이루어지도록 구현됨
    ![qna](https://github.com/user-attachments/assets/170663c3-80c5-4b61-acb0-9b2f972c7496)
    
    
<br><br>

### 추천 시스템
#### **사용자 증상기반 약초 추천 기능**
  - 사용자가 입력한 증상이나 관심사를 바탕으로 적절한 약초 추천
    - 개화기, 증상, 사용부위 등의 정보가 임베딩 처리되어, 증상 기반뿐만 아니라 사용자의 다른 관심사를 기반으로도 약초를 추천받을 수 있도록 처리 됨.
    - PostgreSQL의 PGVector를 통해 코사인 유사도 검색을 수행하고, 약초DB 기반으로 신뢰성과 정확성을 높인 정보 제공
      ![ai](https://github.com/user-attachments/assets/ddec1248-14fd-4cac-aa00-a792fa133ca7)

<br><br>
  - 백엔드 단에서 프롬프트 처리를 고도화하여 사용자의 증상에 가장 적합한 약초부터 우선순위를 부여하여 추천하도록 구현

    ![image](https://github.com/user-attachments/assets/01f59936-a80a-4160-b9f7-5f1062f74fc6)

#### **사용자 증상 기반 약초 추천 클러스터링**
  - 비고: 백엔드 구현 완료, 프론트엔드 구현전

### 기타
#### 약초 PDF
  - 사용자가 수집하기를 원하는 약초 정보를 PDF로 미리보기 및 다운로드/출력할 수 있는 기능
    - html2canvas-pro, jsPdf 라이브러리 적용
    ![pdf](https://github.com/user-attachments/assets/b1d6f057-7194-4a44-802f-dfb22e97bbbb)


<br><br>    
## 🧰 기술 스택
| 구분 | 기술 스택 | 비고 |
|--------------|--------------|--------------|
| **Frontend** | `React 19` | 19버전부터 forwardRef 불필요 |
|              | `TypeScript` | 정적 타입 |
|              | `Zustand` | 클라이언트 전역 상태 |
|              | `React Query` | 비동기 서버 상태 관리, 캐싱 및 자동 리페치 처리 |
|              | `Tailwind CSS` | 유틸리티 기반 CSS 프레임워크 |
| **Backend**  | `Spring Boot` | 자바기반 백엔드 프레임워크 |
|              | `Spring Security` | 인증, 인가 및 보안 필터 프레임워크 |
|              | `JPA`, `QueryDSL` | 객체-관계 매핑 및 타입 안전한 동적 쿼리 작성 |
|              | `PostgreSQL (PGVector)` | 벡터 데이터 저장 및 유사도 기반 질의 처리 |
|**AI/ML**     | `Spring AI` | OpenAI 기반 LLM과의 통합 및 AI 기능 개발 |
|              | `OpenAI Embedding` | 사용자 입력 임베딩을 통한 유사도 검색 기반 추천 기능 |
| **ETC**      | `Vite` | 빠른 번들링 및 개발 환경 제공 |
|              | `AWS` | 배포, S3 스토리지, RDS 등 클라우드 인프라 운영 |
|              | `Nginx` | 리버스 프록시, SSL 종료, 클라이언트와 웹 애플리케이션 서버 사이의 중간 계층으로서 악의적 트래픽 차단, IP 로깅 |
|              | `GitHub Actions` | CI/CD 자동화로 빌드, 테스트, 배포 효율화 |

<br><br>
## 🗂️ 프로젝트 구조
### 프론트엔드
```
📦 src/
 ┣ 📂apis               # Axios 기반 API 호출 함수 모음
 ┣ 📂assets             # 정적 파일(이미지, 폰트, 스타일)
 ┃ ┣ 📂fonts
 ┃ ┣ 📂styles
 ┃ ┃ ┗ 📜fonts.css
 ┃ ┣ 📜home-map.png
 ┃ ┣ 📜plant.png
 ┃ ┗ 📜wave.svg
 ┣ 📂components         # 공통 UI 컴포넌트 (재사용성 높은 요소들)
 ┃ ┣ 📂button           # 버튼 UI 모음
 ┃ ┣ 📂card             # 메시지/결과 카드
 ┃ ┣ 📂carousel         # 이미지 슬라이더
 ┃ ┣ 📂editor           # 커뮤니티 글 작성 등 에디터
 ┃ ┣ 📂icon             # 아이콘 컴포넌트 (ex. Logo)
 ┃ ┣ 📂layout           # 레이아웃 관련 구성 요소
 ┃ ┣ 📂pagination       # 페이지네이션 컴포넌트
 ┃ ┗ 📂spinner          # 로딩 스피너
 ┣ 📂config             # API 설정, 상수값, 엔드포인트
 ┃ ┣ 📜api.ts
 ┃ ┣ 📜axios.ts
 ┃ ┣ 📜categories.ts
 ┃ ┣ 📜constants.ts
 ┃ ┣ 📜keys.ts
 ┃ ┗ 📜urls.ts
 ┣ 📂hooks              # 커스텀 훅
 ┃ ┣ 📂mutations        # POST, PUT, DELETE 관련 훅
 ┃ ┣ 📂queries          # GET 관련 훅
 ┃ ┣ 📜useAuth.ts       # 인증 관련 훅
 ┃ ┣ 📜usePathType.ts   # 경로 기반 조건 분기
 ┃ ┣ 📜useRedirection.tsx
 ┃ ┗ 📜useRegisterForm.ts
 ┣ 📂pages              # 라우팅 되는 페이지 UI 구성
 ┃ ┣ 📂Home             # 홈 페이지
 ┃ ┣ 📂Herb             # 약초 리스트
 ┃ ┣ 📂HerbDetail       # 약초 상세 페이지
 ┃ ┣ 📂HerbRecommend    # 추천 결과 페이지
 ┃ ┣ 📂Community        # 커뮤니티 목록
 ┃ ┣ 📂CommunityDetail  # 게시글 상세 + 댓글
 ┃ ┣ 📂CommunityEditor  # 글쓰기 페이지
 ┃ ┣ 📂Login            # 로그인
 ┃ ┣ 📂Register         # 회원가입
 ┃ ┣ 📂FindPassword     # 비밀번호 찾기
 ┃ ┗ 📂Mypage           # 마이페이지
 ┣ 📂service            # API 서비스 레이어 (비즈니스 로직)
 ┣ 📂store              # 전역 상태 관리 (ex. 로그인 상태)
 ┃ ┣ 📂types
 ┃ ┗ 📜loginState.ts
 ┣ 📂types              # 전역 타입 정의 (TS 인터페이스/타입)
 ┣ 📂utils              # 공통 유틸 함수 모음
 ┣ 📜App.tsx            # 라우팅/공통 레이아웃 설정
 ┣ 📜index.css          # 전역 CSS
 ┣ 📜main.tsx           # 진입점
 ┗ 📜vite-env.d.ts      # Vite용 타입 정의

```
### 백엔드
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── mincho/
│   │           └── herb/
│   │               ├── 📂domain/                         # 도메인별 비즈니스 로직 계층
│   │               │   │
│   │               │   ├── 📂bookmark/                    ** # 약초 즐겨찾기 기능 관련 코드**
│   │               │   │   ├── 📂api/                        # 컨트롤러 계층 (REST API 엔드포인트)
│   │               │   │   ├── 📂application/                # 서비스 인터페이스 및 구현체 (비즈니스 로직)
│   │               │   │   ├── 📂domain/                     # 도메인 모델 
│   │               │   │   ├── 📂dto/                        # 요청 및 응답 DTO 클래스
│   │               │   │   ├── 📂entity/                     # JPA 엔티티 클래스
│   │               │   │   └── 📂repository/                 # JPA 및 사용자 정의 리포지토리  ==> 위 구조는 재사용
│   │               │   │
│   │               │   ├── 📂comment/                      # 댓글 기능 관련 모듈
│   │               │   ├── 📂embedding/                    # 임베딩 및 유사도 분석 관련 모듈
│   │               │   ├── 📂herb/                         # 약초 정보 관련 CRUD 기능
│   │               │   ├── 📂like/                         # 좋아요 기능 관련 모듈
│   │               │   ├── 📂notice/                       # 공지사항 게시판 관련 모듈
│   │               │   ├── 📂notification/                 # SSE 알림 기능 관련 모듈
│   │               │   ├── 📂post/                         # 커뮤니티 게시글 관련 기능
│   │               │   ├── 📂qna/                          # QnA 게시판 관련 기능
│   │               │   ├── 📂report/                       # 게시글/댓글 신고 기능
│   │               │   └── 📂user/                         # 사용자 관리 및 인증 관련 기능
│   │               │
│   │               ├── 📂global/                         **# 전역 공통 기능**
│   │               │   ├── 📂aop/                          # AOP 로깅 (유저 활동 기록 등)
│   │               │   ├── 📂base/                         # 공통 엔티티 속성 정의 (생성일 등)
│   │               │   ├── 📂config/                       # 설정 클래스
│   │               │   │   ├── 📂error/                       # 에러 응답 포맷 및 코드
│   │               │   │   ├── 📂success/                     # 성공 응답 포맷 및 코드
│   │               │   │   ├── 📜MailConfig.java              # 메일 발송 설정
│   │               │   │   ├── 📜QuerydslConfig.java          # QueryDSL 설정
│   │               │   │   ├── 📜RedisConfig.java             # Redis 설정
│   │               │   │   ├── 📜S3Config.java                # AWS S3 설정
│   │               │   │   └── 📜SecurityConfig.java          # Spring Security 설정
│   │               │   ├── 📂exception/                    # 전역 예외 처리 정의
│   │               │   ├── 📂io/                           # 외부 통신 서비스 (메일 등)
│   │               │   └── 📂util/                         # 공통 유틸리티 클래스
│   │               ├── 📂infra/                          # 인프라 관련 기능 (헬스체크, 인증 등)
│   │               │   ├── 📂auth/                         # 인프라 인증 연동
│   │               │   └── 📂health/                       # 배포 후 서버 상태 확인용 컨트롤러
│   │               └── 📜HerbApplication.java            # Spring Boot 메인 클래스
│   └── 📂resources/
│       └── 📜application.yml                             # 애플리케이션 환경설정 파일
└── 📂test/                                               # 테스트 코드
```
