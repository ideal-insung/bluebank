package bank.core.User.UserRepository;

import bank.core.domain.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO{

    @Autowired
    private SqlSession sqlSession;

    private static final String NAME_SPACE = "UserDAO.";

    @Override
    public User findByEmail(String email) {
        return sqlSession.selectOne(NAME_SPACE + "findByEmail",email);
    }

    @Override
    public int insertUser(User user) {
        return sqlSession.insert(NAME_SPACE + "insertUser",user);
    }
}
