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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/User")
public class UserContorller {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginInfo,HttpSession httpSession){
        String email = loginInfo.get("email");
        String password = loginInfo.get("password");
        User user = userService.findByEmail(email);
        Map<String, Object> response = new HashMap<>();
        if(user.getEmail().equals(email) && user.getPassword().equals(password)){
            String token = tokenProvider.createToken(user.getUser_id());
            httpSession.setAttribute("user", user);
            response.put("success",true);
            response.put("redirect","/");
            response.put("token",token);
            System.out.println("token = " + token);

            return ResponseEntity.ok(response);
        }else{
            response.put("success",false);
            response.put("message","로그인 실패: 이메일 또는 비밀번호가 틀렸습니다.");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/register")
    public String ToRegister(Model model){
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> UserRegister(@RequestBody User user){
        Map<String,Object> response = new HashMap<>();
        boolean result = userService.insertUser(user);
        if(!result){
            response.put("success", false);
            response.put("message", "이미 등록된 아이디입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashBoard")
    public String dashBoard(Model model){
        return "dashBoard";
    }
}
