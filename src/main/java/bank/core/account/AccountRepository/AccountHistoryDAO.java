package bank.core.account.AccountRepository;

import bank.core.domain.Account;
import bank.core.domain.AccountHistory;

import java.util.List;
import java.util.Map;

public interface AccountHistoryDAO {

    //히스토리 저장
    void saveAccountHistory(AccountHistory accountHistory);

    //히스토리 리스트조회
    List<AccountHistory> getAccountHistory(Map<String,Object> map);

    //히스토리 추가
    int addAccountHistory(Map<String,Object> map);
}
