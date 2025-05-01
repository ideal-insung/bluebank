package bank.core.exeption;

public class TransferException extends RuntimeException{
    public TransferException(String message) {
        super(message);
    }
}
