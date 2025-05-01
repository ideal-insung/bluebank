package bank.core.account.AccountRepository;

import bank.core.domain.Account;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AccountDAOImpl implements AccountDAO{

    @Autowired
    private SqlSession sqlSession;

    private static final String NAME_SPACE = "bank.core.account.AccountRepository.AccountDAO.";

    @Override
    public void saveAccount(Account account) {
        System.out.println("account = " + account);
        sqlSession.insert(NAME_SPACE + "insertAccount",account);
    }

    @Override
    public List<Account> getAccounts(Long userId) {
        return sqlSession.selectList(NAME_SPACE + "selectById", userId);
    }

    @Override
    public Account getAccount(Map<String,Object> map) {
        return sqlSession.selectOne(NAME_SPACE + "selectAccountById", map);
    }

    @Override
    public boolean updateBalance(Map<String, Object> map) {
        int rowsUpdate =  sqlSession.update(NAME_SPACE + "updateBalance",map);
        return rowsUpdate > 0;
    }

    @Override
    public Account getAccountByAccNumber(Map<String, Object> map) {
        return sqlSession.selectOne(NAME_SPACE + "selectByAccNumber", map);
    }
}
