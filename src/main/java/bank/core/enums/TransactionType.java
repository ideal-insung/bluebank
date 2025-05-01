package bank.core.enums;

public enum TransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금");

    private String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static TransactionType fromString(String type) {
        for (TransactionType transactionType : values()) {
            if (transactionType.name().equalsIgnoreCase(type)) {
                return transactionType;
            }
        }
        throw new IllegalArgumentException("Invalid transaction type: " + type);
    }
}
