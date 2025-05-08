package bank.core.User.UserController;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러 클래스
 * 이 클래스는 사용자 홈 화면과 브릿지 페이지를 처리하는 기능을 포함하고 있습니다.
 */
@Controller
public class HomeController {

    /**
     * 홈페이지 요청을 처리하는 메소드 ("/" 경로)
     * @param model 모델 객체, 뷰에 데이터를 전달하기 위해 사용
     * @param session HTTP 세션 객체, 사용자 세션 정보를 관리
     * @return "index" 또는 리다이렉트 경로
     *         - 사용자 세션에 'user' 객체가 없으면 로그인 페이지로 리다이렉트
     *         - 'user' 객체가 있으면 인덱스 페이지로 이동
     */
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // 세션에서 "user" 속성 가져오기
        Object user = session.getAttribute("user");

        // 만약 user가 없으면 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/User/login";
        } else {
            // user 객체가 존재하면 그 값을 출력하고, 모델에 추가하여 index 페이지로 이동
            System.out.println("user = " + user);
            model.addAttribute("user", user);
            return "index";
        }
    }

    /**
     * "/bridge" 경로로 전달된 메시지와 리다이렉트 URL을 처리하는 메소드
     * @param message 요청 파라미터에서 받은 메시지 (선택적)
     * @param redirectURL 요청 파라미터에서 받은 리다이렉트 URL (선택적)
     * @param model 모델 객체, 뷰에 데이터를 전달하기 위해 사용
     * @return "bridge" 브릿지 페이지를 반환
     *         - 전달받은 메시지와 리다이렉트 URL을 모델에 추가하여 뷰로 전달
     */
    @GetMapping("/bridge")
    public String bridge(@RequestParam(value = "message", required = false) String message,
                         @RequestParam(value = "redirectURL", required = false) String redirectURL,
                         Model model) {
        // "/bridge" 요청이 들어왔을 때 받은 파라미터 값 로그로 출력
        System.out.println("/bridge");
        System.out.println("Received message: " + message);
        System.out.println("Received redirectURL: " + redirectURL);

        // 전달받은 메시지와 리다이렉트 URL을 모델에 추가하여 뷰에 전달
        model.addAttribute("message", message);
        model.addAttribute("redirectURL", redirectURL);

        // Bridge 페이지를 반환 (브리지 기능을 위한 HTML 페이지)
        return "bridge";
    }
}
