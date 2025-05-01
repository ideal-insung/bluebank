package bank.core.account.AccountRepository;

import bank.core.domain.AccountHistory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AccountHistoryDAOImpl implements AccountHistoryDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAME_SPACE = "bank.core.account.AccountRepository.AccountHistoryDAO.";

    @Override
    public void saveAccountHistory(AccountHistory accountHistory) {
        sqlSession.insert(NAME_SPACE + "saveAccountHistory",accountHistory);
    }

    @Override
    public List<AccountHistory> getAccountHistory(Map<String, Object> map) {
        return sqlSession.selectList(NAME_SPACE + "selectByAccount", map);
    }

    @Override
    public int addAccountHistory(Map<String, Object> map) {
        return sqlSession.insert(NAME_SPACE + "addAccountHistory", map);
    }
}
