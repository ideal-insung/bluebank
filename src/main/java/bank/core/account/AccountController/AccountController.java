package bank.core.account.AccountController;

import bank.core.account.AccountService.AccountService;
import bank.core.domain.Account;
import bank.core.domain.AccountHistory;
import bank.core.domain.User;
import bank.core.enums.TransactionType;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Account")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @GetMapping("/new")
    public String newAccount(HttpSession httpSession, Model model) {
        model.addAttribute("user", httpSession.getAttribute("user"));
        System.out.println(httpSession.getAttribute("user"));
        return "account/newAccount";
    }

    @PostMapping("/new")
    public String saveAccount(@ModelAttribute Account account, Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        account.setUser_id(user.getUser_id());
        accountService.saveAccount(account);
        redirectAttributes.addAttribute("message", "계좌가 성공적으로 생성되었습니다.");
        redirectAttributes.addAttribute("redirectURL", "/");
        return "redirect:/bridge";
    }

    @GetMapping("/get")
    @ResponseBody // @RestController 아니면 필요
    public List<Account> getAccount(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return accountService.getAccounts(user.getUser_id());
    }

    @GetMapping("/detail")
    public String detailAccount(@RequestParam("id") Long id, HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        Long user_id = user.getUser_id();
        //계좌조회
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("user_id", user_id);
        Account account = accountService.getAccount(map);
        //거래내역 작업추가
        map.put("account_number", account.getAccount_number());
        List<AccountHistory> accountHistory = accountService.getAccountHistory(map);
        model.addAttribute("account", account);
        model.addAttribute("accountHistory", accountHistory);
        return "account/detailAccount";
    }

    @PostMapping("/detail/updateBalance")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBalance(@RequestBody Map<String, Object> updateInfo, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        Long user_id = user.getUser_id();
        updateInfo.put("user_id", user_id);
        String type = (String)updateInfo.get("type");
        TransactionType transactionType = TransactionType.fromString(type);  // Enum으로 변환
        Map<String, Object> response = new HashMap<>();
        try {
            // 서비스 호출하여 입금,출금 처리
            boolean update = accountService.updateBalance(updateInfo);

            if (update) {
                response.put("status", "success");
                response.put("message", transactionType.getDescription()+"이 완료되었습니다.");
                response.put("code", transactionType.getDescription());
                return ResponseEntity.ok(response);  // HTTP 200 OK
            } else {
                //실패
                response.put("status", "failure");
                response.put("message", transactionType.getDescription()+"에 실패하였습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // HTTP 500
            }
        } catch (Exception e) {
            // 예외 발생 시
            response.put("status", "error");
            response.put("message", transactionType.getDescription()+" 처리 중 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // HTTP 500
        }
    }
    @GetMapping("/detail/verifyAcc")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyAcc(@RequestParam Map<String, Object> verifyInfo, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "failure");
            response.put("message", "사용자 세션이 만료되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // HTTP 401 Unauthorized
        }
        Account account = accountService.getAccountByAccNumber(verifyInfo);
        Map<String,Object> response  = new HashMap<>();
        response.put("status"  ,"success");
        response.put("Account" , account);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/detail/transfer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> transfer(@RequestBody Map<String, Object> transferInfo, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "failure");
            response.put("message", "사용자 세션이 만료되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // HTTP 401 Unauthorized
        }
        transferInfo.put("user_id", user.getUser_id());
        boolean result = accountService.transfer(transferInfo);
        Map<String, Object> response = new HashMap<>();
        if(result){
            response.put("status", "success");
            response.put("message", "이체가 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(response);
        }else{
            response.put("status", "failure");
            response.put("message", "이체 처리에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // HTTP 500 Internal Server Error
        }
    }

}