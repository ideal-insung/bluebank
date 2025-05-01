package bank.core.enums;

public enum AccountType {
    SAVINGS("SAV"),        // 저축 계좌
    CHECKING("CHK"),       // 요구불 계좌
    LOAN("LAN"),          // 대출 계좌
    deposit("DEP");       // 예금 계좌

    private final String prefix;

    AccountType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    // 화면에서 받은 String 값으로 enum을 찾는 메서드
    public static AccountType fromString(String type) {
        if (type == null || type.isEmpty()) {
            return AccountType.SAVINGS;  // 기본값으로 "SAV"를 반환
        }

        for (AccountType accountType : AccountType.values()) {
            if (accountType.name().equalsIgnoreCase(type)) {
                return accountType;
            }
        }
        throw new IllegalArgumentException("Unknown account type: " + type);
    }
}
