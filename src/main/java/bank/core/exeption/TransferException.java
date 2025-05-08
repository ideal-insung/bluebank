package bank.core.exeption;

/*
 * 트랜잭션  TransferException
 * */
public class TransferException extends RuntimeException{
    public TransferException(String message) {
        super(message);
    }
}
