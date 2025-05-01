package bank.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class Account {
    private Long id;                 // 계좌 PK
    private Long user_id;             // 사용자(회원) ID
    private String account_number;    // 계좌번호
    private String account_name;      // 계좌명
    private String account_type;      // 계좌 유형 (저축, 입출금 등)
    private String account_holder;    // 소유자명
    private Boolean passbook_issued;  // 통장 발급 여부
    private String contact;          // 연락처 (전화번호/이메일)
    private String account_password;  // 계좌 비밀번호
    private Integer initial_deposit;  // 초기 예금액
    private Integer balance;         // 계좌 잔액
    private Boolean auto_transfer;    // 자동이체 설정 여부
    private Boolean notification;    // 계좌 알림 설정 여부
    private LocalDateTime created_at; // 생성 일시
}
