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

    /**
     * 계좌를 저장합니다.
     * @param account 저장할 계좌 정보
     */
    @Override
    public void saveAccount(Account account) {
        System.out.println("account = " + account);
        sqlSession.insert(NAME_SPACE + "insertAccount",account);
    }

    /**
     * 특정 사용자의 모든 계좌 목록을 조회합니다.
     * @param userId 사용자의 ID
     * @return 사용자의 모든 계좌를 포함한 List<Account>
     */
    @Override
    public List<Account> getAccounts(Long userId) {
        return sqlSession.selectList(NAME_SPACE + "selectById", userId);
    }

    /**
     * 계좌 ID와 사용자 ID를 기반으로 특정 계좌를 조회합니다.
     * @param map 계좌 ID와 사용자 ID를 포함하는 Map (id, user_id)
     * @return 조회된 Account 객체
     */
    @Override
    public Account getAccount(Map<String,Object> map) {
        return sqlSession.selectOne(NAME_SPACE + "selectAccountById", map);
    }

    /**
     * 계좌의 잔액을 업데이트합니다. (입금/출금)
     * @param map 계좌 정보와 잔액 변경 정보가 담긴 Map (account_number, type, amount 등)
     * @return 업데이트 성공 여부 (true: 성공, false: 실패)
     */
    @Override
    public boolean updateBalance(Map<String, Object> map) {
        int rowsUpdate =  sqlSession.update(NAME_SPACE + "updateBalance",map);
        return rowsUpdate > 0;
    }

    /**
     * 계좌 번호를 기반으로 특정 계좌를 조회합니다.
     * @param map 계좌 번호를 포함하는 Map (account_number)
     * @return 조회된 Account 객체
     */
    @Override
    public Account getAccountByAccNumber(Map<String, Object> map) {
        return sqlSession.selectOne(NAME_SPACE + "selectByAccNumber", map);
    }

    /**
     * userId를 기반으로 계좌의 수를 조회합니다.
     * @param userId 계좌의 총개수 (userId)
     * @return 조회된 Account 객체의 수
     */
    @Override
    public int getAccountAllCnt(Long userId) {
        return sqlSession.selectOne(NAME_SPACE + "selectByIdAllCnt", userId);
    }

    @Override
    public boolean deleteAccount(Long id) {
        int rowsDelete =  sqlSession.update(NAME_SPACE + "deleteAccount",id);
        return rowsDelete > 0;
    }
}
