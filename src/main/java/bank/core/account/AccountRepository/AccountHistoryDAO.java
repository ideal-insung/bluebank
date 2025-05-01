package bank.core.account.AccountRepository;

import bank.core.domain.Account;
import bank.core.domain.AccountHistory;

import java.util.List;
import java.util.Map;

public interface AccountHistoryDAO {

    void saveAccountHistory(AccountHistory accountHistory);

    List<AccountHistory> getAccountHistory(Map<String,Object> map);

    int addAccountHistory(Map<String,Object> map);
}
