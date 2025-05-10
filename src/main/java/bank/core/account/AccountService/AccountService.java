package bank.core.account.AccountService;

import bank.core.account.AccountRepository.AccountDAO;
import bank.core.account.AccountRepository.AccountHistoryDAO;
import bank.core.common.utils.StringUtils;
import bank.core.domain.Account;
import bank.core.domain.AccountHistory;
import bank.core.enums.AccountType;
import bank.core.enums.TransactionType;
import bank.core.exeption.AccountNotFoundException;
import bank.core.exeption.InvalidAccountException;
import bank.core.exeption.TransferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AccountService {
    private final AccountDAO accountDAO;
    private final AccountHistoryDAO accountHistoryDAO;
    public static final String DEPOSIT = "DEPOSIT";
    public static final String WITHDRAW = "WITHDRAWAL";

    @Autowired
    public AccountService(AccountDAO accountDAO, AccountHistoryDAO accountHistoryDAO) {
        this.accountDAO = accountDAO;
        this.accountHistoryDAO = accountHistoryDAO;
    }

    /**
     * 새 계좌를 저장하고, 최초 입금을 기록합니다.
     * @param account 계좌 정보
     */
    public void saveAccount(Account account) {
        AccountType accountType = AccountType.fromString(account.getAccount_type());
        account.setAccount_number(generateAccountNumber(accountType));
        account.setBalance(account.getInitial_deposit());
        accountDAO.saveAccount(account);

        // 최초 입금 기록을 account_history 테이블에 추가
        Map<String, Object> map = new HashMap<>();
        map.put("account_number", account.getAccount_number());
        map.put("initial_deposit", account.getInitial_deposit());
        map.put("user_id", account.getUser_id());
        saveInitialDepositHistory(map);
    }

    /**
     * 최초 입금 기록을 account_history 테이블에 저장합니다.
     * @param map 계좌 번호, 사용자 ID, 입금액 등의 정보를 담은 맵
     */
    public void saveInitialDepositHistory(Map<String, Object> map) {
        // 최초 입금 기록을 account_history 테이블에 추가
        AccountHistory history = new AccountHistory();
        history.setAccount_number(String.valueOf(map.get("account_number")));
        history.setUser_id((Long) map.get("user_id"));
        history.setTransaction_type("DEPOSIT");  // 입금
        history.setAmount((Integer) map.get("initial_deposit"));
        history.setBalance((Integer) map.get("initial_deposit"));  // 최초 입금 후 잔액
        history.setTransaction_time(LocalDateTime.now());  // 거래 시간
        history.setDescription("계좌 개설 입금");  // 설명: 계좌 개설 입금

        // AccountHistory DAO를 통해 테이블에 저장
        accountHistoryDAO.saveAccountHistory(history);
    }

    /**
     * 계좌 번호를 생성합니다. 계좌 종류에 맞는 접두어와 랜덤한 숫자를 조합하여 생성합니다.
     * @param accountType 계좌 종류
     * @return 생성된 계좌 번호
     */
    public String generateAccountNumber(AccountType accountType) {
        // 13자리 고유 계좌번호 생성
        StringBuilder accountNumber = new StringBuilder();

        // 접두어 추가 (SAV, CHK, LOAN, DEP 등)
        accountNumber.append(accountType.getPrefix());

        // 10자리의 랜덤 숫자 생성 (예: 1234567890)
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            accountNumber.append(random.nextInt(10));  // 0-9 사이의 숫자 추가
        }
        return accountNumber.toString();
    }

    /**
     * 특정 사용자의 모든 계좌를 조회합니다.
     * @param userId 사용자 ID
     * @return 해당 사용자의 계좌 목록
     */
    public Map<String, Object> getAccounts(Long userId) {
        List<Account> accounts = accountDAO.getAccounts(userId);
        int totalCount = accountDAO.getAccountAllCnt(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("accounts", accounts);
        response.put("totalCount", totalCount);

        return response;
    }

    /**
     * 계좌 번호에 해당하는 계좌를 조회합니다.
     * @param map 계좌 조회에 필요한 정보가 담긴 맵 (예: 계좌 번호)
     * @return 해당 계좌 정보
     */
    public Account getAccount(Map<String, Object> map) {
        return accountDAO.getAccount(map);
    }

    /**
     * 특정 계좌의 거래 내역을 조회합니다.
     * @param map 계좌 번호 등 거래 내역 조회에 필요한 정보가 담긴 맵
     * @return 해당 계좌의 거래 내역 목록
     */
    public List<AccountHistory> getAccountHistory(Map<String, Object> map) {
        return accountHistoryDAO.getAccountHistory(map);
    }

    /**
     * 계좌의 잔액을 업데이트하고, 거래 내역을 기록합니다.
     * @param map 잔액 업데이트에 필요한 정보가 담긴 맵 (예: 계좌 번호, 금액, 거래 유형)
     * @return 업데이트 성공 여부
     */
    @Transactional
    public boolean updateBalance(Map<String, Object> map) {
        try {
            String type = (String) map.get("type");
            Account account = accountDAO.getAccount(map);
            int amount = Integer.parseInt((String) map.get("amount"));
            int balance = 0;

            if (DEPOSIT.equals(type)) { // 입금
                balance = account.getBalance() + amount;
            } else if (WITHDRAW.equals(type)) { // 출금
                if (account.getBalance() < amount) {
                    throw new RuntimeException("잔액이 부족합니다.");
                }
                balance = account.getBalance() - amount;
                map.put("amount", -amount);
            }

            map.put("balance", balance);
            int cnt = accountHistoryDAO.addAccountHistory(map);
            if (cnt == 1) {
                boolean balanceUpdated = accountDAO.updateBalance(map);
                if (balanceUpdated) {
                    return true;
                } else {
                    // 잔액 업데이트 실패 시 예외를 던져 롤백
                    throw new RuntimeException("잔액 업데이트 실패");
                }
            } else {
                // 거래 내역 삽입 실패 시 예외를 던져 롤백
                throw new RuntimeException("거래 내역 삽입 실패");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 계좌 번호로 계좌를 조회하고, 계좌가 유효한지 확인합니다.
     * @param verifyInfo 계좌 검증에 필요한 정보가 담긴 맵 (예: 계좌 번호)
     * @return 계좌 정보
     * @throws AccountNotFoundException 계좌가 존재하지 않으면 예외 발생
     * @throws InvalidAccountException 출금 계좌와 입금 계좌가 동일한 경우 예외 발생
     */
    public Account getAccountByAccNumber(Map<String, Object> verifyInfo) {
        Account account = accountDAO.getAccountByAccNumber(verifyInfo);
        Map<String, Object> response = new HashMap<>();

        if (account == null) {
            throw new AccountNotFoundException("해당하는 계좌가 없습니다.");
        } else {
            String withdrawalAcc = (String) verifyInfo.get("withdrawal_account");
            if (withdrawalAcc != null && withdrawalAcc.equals(account.getAccount_number())) {
                throw new InvalidAccountException("출금 계좌와 입금 계좌가 동일할 수 없습니다.");
            }
            return account;
        }
    }

    /**
     * 출금 계좌에서 금액을 출금하고, 입금 계좌로 금액을 입금하는 이체 작업을 처리합니다.
     * @param transferInfo 이체에 필요한 정보 (출금 계좌, 입금 계좌, 이체 금액 등)
     * @return 이체 성공 여부
     * @throws TransferException 이체 실패 시 예외 발생
     */
    @Transactional
    public boolean transfer(Map<String, Object> transferInfo) {
        // 출금 계좌 번호 객체 생성
        Map<String, Object> fromMap = new HashMap<>();
        fromMap.put("account_number", StringUtils.extractDigits((String) transferInfo.get("fromAccount")));
        Account fromAccount = this.getAccountByAccNumber(fromMap);
        if (fromAccount == null) {
            throw new AccountNotFoundException("출금 계좌를 찾을 수 없습니다.");
        }

        // 입금 계좌 번호 객체 생성
        Map<String, Object> toMap = new HashMap<>();
        toMap.put("account_number", StringUtils.extractDigits((String) transferInfo.get("toAccount")));
        Account toAccount = this.getAccountByAccNumber(toMap);
        if (toAccount == null) {
            throw new AccountNotFoundException("입금 계좌를 찾을 수 없습니다.");
        }

        // 출금 처리
        fromMap.put("amount", transferInfo.get("transferAmount"));
        fromMap.put("type", TransactionType.WITHDRAWAL.name());
        fromMap.put("user_id", transferInfo.get("user_id"));
        fromMap.put("id", fromAccount.getId());
        fromMap.put("target_account_number", toAccount.getAccount_number());
        fromMap.put("description", transferInfo.get("description"));
        fromMap.put("account_number", fromAccount.getAccount_number());
        System.out.println("fromMap = " + fromMap);
        boolean withdrawal = this.updateBalance(fromMap);
        if (!withdrawal) {
            throw new TransferException("출금 처리에 실패했습니다.");
        }

        // 입금 처리
        toMap.put("amount", transferInfo.get("transferAmount"));
        toMap.put("type", TransactionType.DEPOSIT.name());
        toMap.put("user_id", toAccount.getUser_id());
        toMap.put("id", toAccount.getId());
        toMap.put("account_number", toAccount.getAccount_number());
        toMap.put("target_account_number", fromAccount.getAccount_number());
        toMap.put("description", transferInfo.get("description"));
        boolean deposit = this.updateBalance(toMap);
        if (!deposit) {
            throw new TransferException("입금 처리에 실패했습니다.");
        }

        return true;
    }

    @Transactional
    public boolean markAccountAsDeleted(Map<String, Object> transferInfo) {
        // 삭제할 계좌 객체 생성
        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put("account_number", StringUtils.extractDigits((String) transferInfo.get("fromAccount")));
        Account account = accountDAO.getAccountByAccNumber(deleteMap);
        if (account == null) {
            throw new AccountNotFoundException("출금 계좌를 찾을 수 없습니다.");
        }
        return accountDAO.deleteAccount(account.getId());
    }
}
