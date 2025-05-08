package bank.core.User.UserRepository;

import bank.core.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO {

    //이메일로 user조회
    User findByEmail(String email);

    //user추가
    int insertUser(User user);
}
