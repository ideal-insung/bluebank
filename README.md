
# 🏦 BlueBank

Java 기반의 간단한 금융 서비스 웹 애플리케이션입니다.  
계좌 조회, 입출금, 이체, OTP 인증 등 실생활의 은행 기능을 간소화하여 구현하였습니다.

---

## 📌 주요 기능
1. 회원가입
 - 이메일, 이름, 비밀번호, 비밀번호 확인, 전화번호를 입력하여 회원가입을 할 수 있다.
 - 이메일은 중복 불가 하다.

2. 로그인
 - 회원가입시 사용한 이메일과 비밀번호로 로그인 할 수 있다.
 - 회원가입과 로그인을 제외한 BlueBank 프로그램의 모든 기능은 로그인 후 가능하다.

3. 계좌 추가
 - 계좌생성은 bluebank 은행의 계좌만 가능하다.
 - 계좌명, 계좌유형, 소유자, 비밀번호 설정, 초기 예금액 등 입력하여 계좌를 추가할 수 있다.
 - 초기 예금액 입력시 메인화면에 해당 잔액이 노출된다.

4. 계좌 상세 (입금, 출금, 이체)
 - 원하는 계좌에 입출금 및 이체를 OTP를 통하여 입출금 할 수 있다.
 - 입출금시 입출금 금액, 입금 사유를 입력하여 입출금 할 수 있다.
 - 이체시 본인 계좌의 잔액 내에서 송금을 할 수 있다.
 - 이체시 입금계좌번호를 입력하여 입금자가 맞는지 검증하는 계좌검증로직을 통해 이체금액 및 이체사유를 입력하여 송금을 할 수 있다.
 - 본인의 계좌별 입출금 내역을 조회 할 수 있다.

5. 계좌 삭제
 - 메인에 계좌가 1개일경우 계좌삭제는 불가능하다.
 - 계좌가 1개 이상일경우 남은잔액을 이체받을 계좌를 선택하여 계좌를 삭제할 수 있다.
 - 삭제시 데이터베이스에서 삭제되지 않고 삭제여부가 저장된다.


---

## 🛠️ 사용 기술 스택

| 분류      | 기술 |
|-----------|------|
| Language  | Java 17 |
| Framework | Spring, Spring MVC, MyBatis |
| View      | Thymeleaf, HTML/CSS, JavaScript,|
| DB        | MySQL |
| Build     | Gradle |
| Deploy    | Local Tomcat (개발환경 기준) |
| 기타      | Lombok, Git, GitHub |

---

## 📂 프로젝트 구조 (예시)

```
bluebank/
├── controller/
├── service/
├── dao/
├── domain/
├── mapper/
├── resources/
│   └── mapper/
│   └── templates/
└── static/
```


## 🧑‍💻 개발자

- **황인성 (Insung Hwang)**  
- 블로그: [https://ideal823.tistory.com](https://ideal823.tistory.com)  
- GitHub: [https://github.com/ideal-insung](https://github.com/ideal-insung)

---

## 💬 목적 및 학습 포인트

- 금융 도메인에 특화된 프로세스 (OTP 등) 직접 구현
- MyBatis 기반 SQL 관리 및 동적 쿼리 작성 경험
- 도메인/서비스/컨트롤러 계층 분리 및 트랜잭션 관리


---

## 🔒 보안 관련 참고

- OTP는 시간 기반 만료 로직 구현
- 로그인 시 세션/쿠키 기반 간단한 인증 처리 (Spring Security 미사용)
- 추후 Spring Security 및 JWT 인증 방식 연동 예정
