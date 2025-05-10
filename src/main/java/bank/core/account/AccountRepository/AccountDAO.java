package bank.core.account.AccountRepository;

import bank.core.domain.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

public interface AccountDAO {

    //계좌저장
    void saveAccount(Account account);

    //특정 사용자의 모든 계좌 목록조회
    List<Account> getAccounts(Long userId);

    //계좌 ID와 사용자 ID를 기반으로 특정 계좌를 조회합니다.
    Account getAccount(Map<String,Object> map);

    //계좌의 잔액을 업데이트합니다. (입금/출금)
    boolean updateBalance(Map<String,Object> map);

    //계좌 번호를 기반으로 특정 계좌를 조회합니다.
    Account getAccountByAccNumber(Map<String,Object> map);

    //특정 사용자의 모든 계좌 수를 조회
    int getAccountAllCnt(Long userId);

    boolean deleteAccount(Long id);
}
