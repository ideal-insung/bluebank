package bank.core.User.UserService;

import bank.core.User.UserRepository.UserDAO;
import bank.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 이 클래스는 사용자 등록 및 조회와 같은 주요 비즈니스 로직을 구현합니다.
 */
@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;  // 사용자 정보를 처리하는 DAO 객체

    /**
     * 새 사용자를 DB에 등록하는 메소드
     * @param user 등록할 사용자 정보가 담긴 User 객체
     * @return 사용자 이메일이 이미 존재하면 false를 반환하고, 그렇지 않으면 true를 반환
     *         - 이메일 중복 체크 후, 중복이 없으면 사용자 등록 수행
     */
    public boolean insertUser(User user){
        // 이미 등록된 이메일이 있는지 확인
        User findByPhoneUser = userDAO.findByEmail(user.getEmail());
        if(findByPhoneUser != null){
            return false;  // 이메일이 이미 존재하면 false 반환
        }

        userDAO.insertUser(user);  // 이메일이 중복되지 않으면 사용자 등록
        return true;  // 등록 성공
    }

    /**
     * 이메일을 사용하여 사용자 정보를 조회하는 메소드
     * @param email 조회할 사용자의 이메일 주소
     * @return 해당 이메일에 맞는 사용자 정보를 담은 User 객체
     *         - 사용자가 존재하지 않으면 null 반환
     */
    public User findByEmail(String email){
        return userDAO.findByEmail(email);  // 이메일을 기준으로 사용자 조회
    }
}
