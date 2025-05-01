package bank.core.account.AccountRepository;

import bank.core.domain.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

public interface AccountDAO {

    void saveAccount(Account account);

    List<Account> getAccounts(Long userId);

    Account getAccount(Map<String,Object> map);

    boolean updateBalance(Map<String,Object> map);

    Account getAccountByAccNumber(Map<String,Object> map);
}
