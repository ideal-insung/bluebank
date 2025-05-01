package bank.core.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    private Long user_id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private LocalDateTime created_at;

}
