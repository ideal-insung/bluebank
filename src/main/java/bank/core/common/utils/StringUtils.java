package bank.core.common.utils;

public class StringUtils {
    /**
     * 문자열에서 숫자만 추출합니다.
     * 예: "CHK2129586300" → "2129586300"
     *
     * @param input 원본 문자열
     * @return 숫자만 남긴 문자열
     */
    public static String extractDigits(String input) {
        if (input == null) return null;
        return input.replaceAll("\\D", ""); // \\D는 숫자가 아닌 문자
    }
}
