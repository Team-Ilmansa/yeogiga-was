# 🌍 Yeogiga (여기가)

## 1️⃣ 프로젝트 소개

**Yeogiga(여기가)** 는 여행 중 생성되는 다양한 정보(일정, 장소, 이미지, 이동 경로 등)를  
저장하고, 여행 단위로 관리할 수 있도록 설계된 **여행 기록 및 관리 플랫폼**입니다.

---

## 2️⃣ 팀원 정보

<table align="center">
  <tr>
    <td align="center" width="180">
      <img src="https://avatars.githubusercontent.com/u/165015500?v=4" width="120" height="120"/><br />
      <b>UX/UI</b><br />
      <a href="https://github.com/9ooi"><b>김규희</b></a>
    </td>
    <td align="center" width="180">
      <img src="https://avatars.githubusercontent.com/u/63886776?v=4" width="120" height="120"/><br />
      <b>APP</b><br />
      <a href="https://github.com/NoGravyBeef"><b>이민엽</b></a>
    </td>
    <td align="center" width="180">
      <img src="https://avatars.githubusercontent.com/u/164734665?v=4" width="120" height="120"/><br />
      <b>FE</b><br />
      <a href="https://github.com/gugitgugit"><b>구준혁</b></a>
    </td>
    <td align="center" width="180">
      <img src="https://avatars.githubusercontent.com/u/151600782?v=4" width="120" height="120"/><br />
      <b>FE</b><br />
      <a href="https://github.com/juiuj"><b>김주희</b></a>
    </td>
    <td align="center" width="180">
      <img src="https://avatars.githubusercontent.com/u/125343432?v=4" width="120" height="120"/><br />
      <b>BE / Leader</b><br />
      <a href="https://github.com/daeyoung0726"><b>박대영</b></a>
    </td>
    <td align="center" width="180">
      <img src="https://avatars.githubusercontent.com/u/128910345?v=4" width="120" height="120"/><br />
      <b>BE</b><br />
      <a href="https://github.com/hky035"><b>허기영</b></a>
    </td>
  </tr>
</table>

---

## 3️⃣ 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot, Spring Security, Spring Data JPA |
| **Database** | MySQL, MongoDB, Redis |
| **Infra & DevOps** | AWS (EC2, RDS, S3, Lambda), Docker, GitHub Actions |
| **Notification** | Firebase Cloud Messaging (FCM) |

---

## 4️⃣ Directory Structure

```text
src/main/java/kr/co/yeogiga
├── application                    # 유스케이스 계층 (비즈니스 흐름 조합)
├── domain                         # 도메인 계층 (핵심 비즈니스 로직)
├── infrastructure                 # 인프라 계층 (외부 시스템 및 기술 연동 계층)
├── presentation                   # 프레젠테이션 계층 (API)
└── common                         # 공통 모듈
```

---

## 5️⃣ ERD

### 🗄️ RDB (MySQL)

<img src="https://github.com/user-attachments/assets/11c8ba29-4ec3-4fce-af6c-16906f1241e1" width="100%" />

### 📦 NoSQL (MongoDB)

> 이미지 및 위치 기반 데이터를 유연하게 저장하기 위해 MongoDB를 사용합니다.

<img src="https://github.com/user-attachments/assets/65b16a4f-3cc1-4cf0-8713-0d3b55965b2c" width="420" />

---

## 6️⃣ Infra Architecture

<img width="1253" height="741" alt="yeogiga" src="https://github.com/user-attachments/assets/50b20003-948d-4335-94df-b5ff146eb226" />


