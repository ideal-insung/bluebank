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

    /**
     * 새 계좌 생성 페이지를 표시합니다.
     *
     * @param httpSession 현재 세션 정보를 담고 있는 HttpSession 객체
     * @param model 모델에 사용자 정보를 추가하여 화면에 전달
     * @return 계좌 생성 페이지의 뷰 이름
     */
    @GetMapping("/new")
    public String newAccount(HttpSession httpSession, Model model) {
        model.addAttribute("user", httpSession.getAttribute("user"));
        System.out.println(httpSession.getAttribute("user"));
        return "account/newAccount";  // 계좌 생성 페이지로 이동
    }

    /**
     * 새 계좌를 생성하여 데이터베이스에 저장합니다.
     *
     * @param account 새로 생성할 계좌 정보를 담고 있는 Account 객체
     * @param model 모델에 데이터 추가
     * @param httpSession 현재 세션 정보
     * @param redirectAttributes 리다이렉트 시 메시지를 전달하기 위한 객체
     * @return 계좌 생성 후 리다이렉트될 페이지 URL
     */
    @PostMapping("/new")
    public String saveAccount(@ModelAttribute Account account, Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        account.setUser_id(user.getUser_id());  // 로그인한 사용자의 ID를 계좌에 설정
        accountService.saveAccount(account);  // 계좌 저장
        redirectAttributes.addAttribute("message", "계좌가 성공적으로 생성되었습니다.");
        redirectAttributes.addAttribute("redirectURL", "/");
        return "redirect:/bridge";  // 계좌 생성 후 "bridge" 페이지로 리다이렉트
    }

    /**
     * 로그인한 사용자의 계좌 목록을 반환합니다.
     *
     * @param httpSession 현재 세션 정보를 담고 있는 HttpSession 객체
     * @return 사용자의 계좌 목록을 담고 있는 List<Account>
     * @throws IllegalStateException 로그인되지 않은 경우 예외를 발생시킴
     */
    @GetMapping("/get")
    @ResponseBody
    public List<Account> getAccount(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return accountService.getAccounts(user.getUser_id());  // 사용자의 계좌 목록 반환
    }

    /**
     * 특정 계좌의 상세 정보를 조회합니다.
     *
     * @param id 조회할 계좌의 ID
     * @param httpSession 현재 세션 정보
     * @param model 조회한 계좌 정보와 거래 내역을 담을 모델
     * @return 계좌 상세 정보 페이지로 이동
     */
    @GetMapping("/detail")
    public String detailAccount(@RequestParam("id") Long id, HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        Long user_id = user.getUser_id();

        // 계좌 정보 조회
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("user_id", user_id);
        Account account = accountService.getAccount(map);

        // 해당 계좌의 거래 내역 조회
        map.put("account_number", account.getAccount_number());
        List<AccountHistory> accountHistory = accountService.getAccountHistory(map);

        model.addAttribute("account", account);
        model.addAttribute("accountHistory", accountHistory);
        return "account/detailAccount";  // 계좌 상세 페이지로 이동
    }

    /**
     * 계좌 잔액을 업데이트하는 메서드 (입금/출금).
     *
     * @param updateInfo 잔액을 업데이트할 정보가 담긴 Map (입금/출금 유형 및 금액 포함)
     * @param httpSession 현재 세션 정보
     * @return 잔액 업데이트 처리 결과를 담은 ResponseEntity
     */
    @PostMapping("/detail/updateBalance")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateBalance(@RequestBody Map<String, Object> updateInfo, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        Long user_id = user.getUser_id();
        updateInfo.put("user_id", user_id);
        String type = (String) updateInfo.get("type");
        TransactionType transactionType = TransactionType.fromString(type);  // Enum으로 변환

        Map<String, Object> response = new HashMap<>();
        try {
            // 서비스 호출하여 입금/출금 처리
            boolean update = accountService.updateBalance(updateInfo);

            if (update) {
                response.put("status", "success");
                response.put("message", transactionType.getDescription() + "이 완료되었습니다.");
                response.put("code", transactionType.getDescription());
                return ResponseEntity.ok(response);  // HTTP 200 OK
            } else {
                // 실패
                response.put("status", "failure");
                response.put("message", transactionType.getDescription() + "에 실패하였습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // HTTP 500
            }
        } catch (Exception e) {
            // 예외 발생 시
            response.put("status", "error");
            response.put("message", transactionType.getDescription() + " 처리 중 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // HTTP 500
        }
    }

    /**
     * 계좌 번호 유효성 검사 (계좌 번호가 존재하는지 확인).
     *
     * @param verifyInfo 계좌 번호 확인을 위한 정보 (계좌 번호 포함)
     * @param httpSession 현재 세션 정보
     * @return 계좌 유효성 검사 결과를 담은 ResponseEntity
     */
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

        Account account = accountService.getAccountByAccNumber(verifyInfo);  // 계좌번호로 계좌 조회
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("Account", account);
        return ResponseEntity.ok(response);  // 계좌 정보 반환
    }

    /**
     * 계좌 이체 처리를 수행합니다.
     *
     * @param transferInfo 이체 정보가 담긴 Map (이체할 계좌 정보 포함)
     * @param httpSession 현재 세션 정보
     * @return 이체 처리 결과를 담은 ResponseEntity
     */
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

        transferInfo.put("user_id", user.getUser_id());  // 로그인한 사용자의 ID를 이체 정보에 추가
        boolean result = accountService.transfer(transferInfo);  // 이체 처리
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("status", "success");
            response.put("message", "이체가 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(response);  // HTTP 200 OK
        } else {
            response.put("status", "failure");
            response.put("message", "이체 처리에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // HTTP 500 Internal Server Error
        }
    }
}