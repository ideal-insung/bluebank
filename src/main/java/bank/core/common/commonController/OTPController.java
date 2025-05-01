package bank.core.common.commonController;

import bank.core.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/OTP")
public class OTPController {

    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateOtp(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "failure");
            response.put("message", "사용자 세션이 만료되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // HTTP 401 Unauthorized
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        httpSession.setAttribute("otp", otp);
        httpSession.setAttribute("otpExpiresAt", System.currentTimeMillis() + 5 * 60 * 1000); // 5분
        Map<String, Object> response = new HashMap<>();
        response.put("otp", otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify_otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> payload, HttpSession session) {
        String enteredOtp = payload.get("otp");
        String sessionOtp = (String) session.getAttribute("otp");
        Long expiresAt = (Long) session.getAttribute("otpExpiresAt");
        Map<String, Object> response = new HashMap<>();

        if (sessionOtp == null || expiresAt == null || System.currentTimeMillis() > expiresAt) {
            response.put("status", "failure");
            response.put("message", "OTP가 만료되었습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!sessionOtp.equals(enteredOtp)) {
            response.put("status", "failure");
            response.put("message", "OTP가 불일치 합니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 성공 시 OTP 제거
        session.removeAttribute("otp");
        session.removeAttribute("otpExpiresAt");

        response.put("status" , "success");
        response.put("message", "OTP 인증 성공");
        return ResponseEntity.ok(response);
    }
}
