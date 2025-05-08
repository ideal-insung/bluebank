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

    /**
     * 계좌의 거래 내역을 저장합니다.
     * @param accountHistory 저장할 거래 내역 정보
     */
    @Override
    public void saveAccountHistory(AccountHistory accountHistory) {
        sqlSession.insert(NAME_SPACE + "saveAccountHistory",accountHistory);
    }

    /**
     * 특정 계좌에 대한 거래 내역을 조회합니다.
     * @param map 계좌 정보가 담긴 Map (account_number 등)
     * @return 해당 계좌의 거래 내역을 포함한 List<AccountHistory>
     */
    @Override
    public List<AccountHistory> getAccountHistory(Map<String, Object> map) {
        return sqlSession.selectList(NAME_SPACE + "selectByAccount", map);
    }

    /**
     * 거래 내역을 추가합니다.
     * @param map 거래 내역을 포함하는 Map (account_number, type, amount 등)
     * @return 추가된 거래 내역의 수 (정상적으로 추가되면 1 이상 반환)
     */
    @Override
    public int addAccountHistory(Map<String, Object> map) {
        return sqlSession.insert(NAME_SPACE + "addAccountHistory", map);
    }
}
