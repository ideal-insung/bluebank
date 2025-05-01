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
public class AccountHistory {
    private Long history_id;                 // 기본키
    private String account_number;           // 계좌번호
    private Long user_id;                    // 유저 아이디
    private String transaction_type;         // 거래 유형 (입금, 출금, 이체 등)
    private Integer amount;                     // 거래 금액
    private Integer balance;                    // 거래 후 잔액
    private LocalDateTime transaction_time;  // 거래 시간
    private String target_account_number;    // 이체 대상 계좌번호 (출금/이체 시)
    private String description;              // 거래 설명 (예: "ATM 출금", "계좌 개설 입금" 등)
}
