package bank.core.exeption;
/*
* 계좌가 없을때 AccountNotFoundException
* */
public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String message) {
        super(message);
    }
}
