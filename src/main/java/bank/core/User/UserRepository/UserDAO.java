package bank.core.User.UserRepository;

import bank.core.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO {

    User findByEmail(String email);

    int insertUser(User user);
}
