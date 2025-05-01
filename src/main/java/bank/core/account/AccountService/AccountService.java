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

    public void saveAccount(Account account){
        AccountType accountType = AccountType.fromString(account.getAccount_type());
        account.setAccount_number(generateAccountNumber(accountType));
        account.setBalance(account.getInitial_deposit());
        accountDAO.saveAccount(account);
        // 최초 입금 기록을 account_history 테이블에 추가
        Map<String,Object> map = new HashMap<>();
        map.put("account_number", account.getAccount_number());
        map.put("initial_deposit", account.getInitial_deposit());
        map.put("user_id", account.getUser_id());
        saveInitialDepositHistory(map);
    }

    public void saveInitialDepositHistory(Map<String,Object>map) {
        // 최초 입금 기록을 account_history 테이블에 추가
        AccountHistory history = new AccountHistory();
        history.setAccount_number(String.valueOf(map.get("account_number")));
        history.setUser_id((Long) map.get("user_id"));
        history.setTransaction_type("DEPOSIT");  // 입금
        history.setAmount((Integer)map.get("initial_deposit"));
        history.setBalance((Integer)map.get("initial_deposit"));  // 최초 입금 후 잔액
        history.setTransaction_time(LocalDateTime.now());  // 거래 시간
        history.setDescription("계좌 개설 입금");  // 설명: 계좌 개설 입금

        // AccountHistory DAO를 통해 테이블에 저장
        accountHistoryDAO.saveAccountHistory(history);
    }
    public String generateAccountNumber(AccountType accountType) {
        // 13자리 고유 계좌번호 생성
        StringBuilder accountNumber = new StringBuilder();

        // 접두어 추가 (SAV, CHK, LOAN,DEP 등)
        accountNumber.append(accountType.getPrefix());

        // 10자리의 랜덤 숫자 생성 (예: 1234567890)
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            accountNumber.append(random.nextInt(10));  // 0-9 사이의 숫자 추가
        }
        return accountNumber.toString();
    }

    public List<Account> getAccounts(Long userId) {
        return accountDAO.getAccounts(userId);
    }

    public Account getAccount(Map<String,Object>map) {
        return accountDAO.getAccount(map);
    }

    public List<AccountHistory> getAccountHistory(Map<String,Object>map) {
        return accountHistoryDAO.getAccountHistory(map);
    }

    @Transactional
    public boolean updateBalance (Map<String, Object>map){
        try {
            String type = (String)map.get("type");
            Account account = accountDAO.getAccount(map);
            int amount = Integer.parseInt((String) map.get("amount"));
            int balance = 0;
            if(DEPOSIT.equals(type)){ // 입금
                balance = account.getBalance() + amount;
            }else if(WITHDRAW.equals(type)){ //출금
                if (account.getBalance() < amount) {
                    throw new RuntimeException("잔액이 부족합니다.");
                }
                balance = account.getBalance() - amount;
                map.put("amount", -amount);
            }

            map.put("balance",balance);
            int cnt = accountHistoryDAO.addAccountHistory(map);
            if(cnt == 1){
                boolean balanceUpdated  = accountDAO.updateBalance(map);
                if(balanceUpdated){
                    return true;
                } else {
                    // 잔액 업데이트 실패 시 예외를 던져 롤백
                    throw new RuntimeException("잔액 업데이트 실패");
                }
            } else {
                // 거래 내역 삽입 실패 시 예외를 던져 롤백
                throw new RuntimeException("거래 내역 삽입 실패");
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Account getAccountByAccNumber(Map<String,Object>verifyInfo) {
        Account account = accountDAO.getAccountByAccNumber(verifyInfo);
        Map<String, Object> response = new HashMap<>();
        if(account == null){
            throw new AccountNotFoundException("해당하는 계좌가 없습니다.");
        }else{
            String withdrawalAcc = (String) verifyInfo.get("withdrawal_account");
            if(withdrawalAcc != null && withdrawalAcc.equals(account.getAccount_number())){
                throw new InvalidAccountException("출금 계좌와 입금 계좌가 동일할 수 없습니다.");
            }
            return account;
        }
    }

    @Transactional
    public boolean transfer(Map<String, Object> transferInfo){
        //출금계좌번호 객체생성
        Map<String,Object> fromMap = new HashMap<>();
        //계좌번호 숫자만으로 추출 후 세팅
        fromMap.put("account_number", StringUtils.extractDigits((String)transferInfo.get("fromAccount")));
        Account fromAccount = this.getAccountByAccNumber(fromMap);
        if (fromAccount == null) {
            throw new AccountNotFoundException("출금 계좌를 찾을 수 없습니다.");
        }

        //입금계좌번호 객체생성
        Map<String,Object> toMap = new HashMap<>();
        toMap.put("account_number", StringUtils.extractDigits((String)transferInfo.get("toAccount")));
        Account toAccount = this.getAccountByAccNumber(toMap);
        if (toAccount == null) {
            throw new AccountNotFoundException("입금 계좌를 찾을 수 없습니다.");
        }

        //출금시작
        fromMap.put("amount",transferInfo.get("transferAmount"));
        fromMap.put("type", TransactionType.WITHDRAWAL.name());
        fromMap.put("user_id",transferInfo.get("user_id"));
        fromMap.put("id",fromAccount.getId());
        fromMap.put("target_account_number",toAccount.getAccount_number());
        fromMap.put("description",transferInfo.get("description"));
        //숫자만넘기면 안되므로 account 객체에서 빼서 세팅
        fromMap.put("account_number",fromAccount.getAccount_number());
        boolean withdrawal = this.updateBalance(fromMap);
        if (!withdrawal) {
            throw new TransferException("출금 처리에 실패했습니다.");
        }

        //입금시작
        toMap.put("amount",transferInfo.get("transferAmount"));
        toMap.put("type",TransactionType.DEPOSIT.name());
        toMap.put("user_id",toAccount.getUser_id());
        toMap.put("id",toAccount.getId());
        toMap.put("account_number",toAccount.getAccount_number());
        toMap.put("target_account_number",fromAccount.getAccount_number());
        toMap.put("description",transferInfo.get("description"));
        boolean deposit = this.updateBalance(toMap);
        if (!deposit) {
            throw new TransferException("입금 처리에 실패했습니다.");
        }

        return true;
    }
}
