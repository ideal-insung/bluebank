package bank.core.User.UserController;

import bank.core.User.UserService.UserService;
import bank.core.domain.User;
import bank.core.security.TokenProvider;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러 클래스
 * 이 클래스는 로그인, 회원가입, 대시보드 등 사용자 관련 기능을 처리합니다.
 */
@Controller
@RequestMapping("/User")
public class UserContorller {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    /**
     * 로그인 페이지 요청을 처리하는 메소드 ("/login" 경로)
     * @return 로그인 페이지를 반환
     */
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    /**
     * 로그인 요청을 처리하는 메소드 ("/login" 경로)
     * @param loginInfo 로그인 정보(이메일, 비밀번호)를 담고 있는 Map
     * @param httpSession HTTP 세션 객체, 로그인한 사용자 정보를 세션에 저장
     * @return 로그인 성공 또는 실패에 대한 응답을 담은 ResponseEntity
     *         - 성공 시 사용자 정보를 세션에 저장하고, 토큰을 생성하여 반환
     *         - 실패 시 오류 메시지를 포함한 응답 반환
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginInfo, HttpSession httpSession){
        String email = loginInfo.get("email");
        String password = loginInfo.get("password");
        User user = userService.findByEmail(email);
        Map<String, Object> response = new HashMap<>();

        // 이메일과 비밀번호가 일치하는 경우 로그인 성공
        if(user.getEmail().equals(email) && user.getPassword().equals(password)){
            String token = tokenProvider.createToken(user.getUser_id());
            httpSession.setAttribute("user", user);  // 세션에 사용자 정보 저장
            response.put("success", true);
            response.put("redirect", "/");  // 로그인 후 리다이렉트할 URL
            response.put("token", token);  // 생성된 토큰 반환
            System.out.println("token = " + token);

            return ResponseEntity.ok(response);  // 성공 응답 반환
        } else {
            // 이메일 또는 비밀번호 불일치 시 로그인 실패
            response.put("success", false);
            response.put("message", "로그인 실패: 이메일 또는 비밀번호가 틀렸습니다.");
            return ResponseEntity.ok(response);  // 실패 응답 반환
        }
    }

    /**
     * 회원가입 페이지 요청을 처리하는 메소드 ("/register" 경로)
     * @param model 모델 객체, 뷰에 데이터를 전달하기 위해 사용
     * @return 회원가입 페이지를 반환
     */
    @GetMapping("/register")
    public String ToRegister(Model model){
        return "register";
    }

    /**
     * 회원가입 요청을 처리하는 메소드 ("/register" 경로)
     * @param user 사용자 정보를 담고 있는 User 객체
     * @return 회원가입 성공 또는 실패에 대한 응답을 담은 ResponseEntity
     *         - 성공 시 사용자 등록 완료 메시지를 반환
     *         - 실패 시 이미 등록된 아이디라는 메시지를 반환
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> UserRegister(@RequestBody User user){
        Map<String, Object> response = new HashMap<>();
        boolean result = userService.insertUser(user);  // 사용자 등록 시도

        // 이미 등록된 아이디가 있는 경우
        if(!result){
            response.put("success", false);
            response.put("message", "이미 등록된 아이디입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);  // 실패 응답 반환
        }

        // 사용자 등록 성공
        response.put("success", true);
        return ResponseEntity.ok(response);  // 성공 응답 반환
    }

    /**
     * 대시보드 페이지 요청을 처리하는 메소드 ("/dashBoard" 경로)
     * @param model 모델 객체, 뷰에 데이터를 전달하기 위해 사용
     * @return 대시보드 페이지를 반환
     */
    @GetMapping("/dashBoard")
    public String dashBoard(Model model){
        return "dashBoard";
    }
}
