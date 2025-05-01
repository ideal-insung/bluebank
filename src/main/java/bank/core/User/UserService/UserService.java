package bank.core.User.UserService;

import bank.core.User.UserRepository.UserDAO;
import bank.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public boolean insertUser(User user){
        User findByPhoneUser = userDAO.findByEmail(user.getEmail());
        if(findByPhoneUser != null){
            return false;
        }
        userDAO.insertUser(user);
        return true;
    }

    public User findByEmail(String email){
        return userDAO.findByEmail(email);
    }
}
