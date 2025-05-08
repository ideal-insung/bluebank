package bank.core.User.UserRepository;

import bank.core.domain.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * UserDAO 인터페이스의 구현체로, MyBatis를 사용하여 사용자 정보를 DB에 조회 및 삽입하는 클래스
 *
 * 이 클래스는 데이터베이스와의 상호작용을 담당하며, 사용자 관련 데이터를 처리합니다.
 */
@Repository
public class UserDAOImpl implements UserDAO {

    @Autowired
    private SqlSession sqlSession;  // MyBatis SqlSession 객체

    private static final String NAME_SPACE = "UserDAO.";  // MyBatis 네임스페이스

    /**
     * 이메일을 사용하여 사용자 정보를 조회하는 메소드
     *
     * @param email 사용자의 이메일 주소
     * @return 이메일에 해당하는 사용자의 정보가 담긴 User 객체
     *         - 사용자 정보를 찾을 수 없으면 null 반환
     */
    @Override
    public User findByEmail(String email) {
        return sqlSession.selectOne(NAME_SPACE + "findByEmail", email);  // SQL 쿼리 실행
    }

    /**
     * 새로운 사용자 정보를 DB에 삽입하는 메소드
     *
     * @param user 삽입할 사용자 정보가 담긴 User 객체
     * @return 삽입된 레코드의 수
     *         - 1이면 삽입 성공, 0이면 삽입 실패
     */
    @Override
    public int insertUser(User user) {
        return sqlSession.insert(NAME_SPACE + "insertUser", user);  // SQL 쿼리 실행
    }
}
