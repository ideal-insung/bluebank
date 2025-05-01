package bank.core.User.UserController;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, HttpSession session){
        Object user = session.getAttribute("user");
        if(user == null){
            return "redirect:/User/login";
        }else{
            System.out.println("user = " + user);
            model.addAttribute("user",user);
            return "index";
        }
    }

    @GetMapping("/bridge")
    public String bridge(@RequestParam(value = "message", required = false) String message,
                         @RequestParam(value = "redirectURL", required = false) String redirectURL,Model model){
        // 로그로 받은 값 확인
        System.out.println("/bridge");
        System.out.println("Received message: " + message);
        System.out.println("Received redirectURL: " + redirectURL);
        // 메시지와 리다이렉트 URL을 그대로 사용할 수 있도록 전달
        model.addAttribute("message", message);
        model.addAttribute("redirectURL", redirectURL);

        // Bridge HTML 페이지를 반환
        return "bridge";
    }
}
