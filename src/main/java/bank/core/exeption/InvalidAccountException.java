package bank.core.exeption;

/*
 * 벨리데이션  InvalidAccountException
 * */
public class InvalidAccountException extends RuntimeException{
    public InvalidAccountException(String message) {
        super(message);
    }
}
