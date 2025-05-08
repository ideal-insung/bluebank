package bank.core.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// 모든 컨트롤러에 대해 전역적으로 예외를 처리하는 클래스
@ControllerAdvice
public class GlobalExceptionHandler {

    // AccountNotFoundException 발생 시 처리하는 메소드
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFoundException(AccountNotFoundException ex) {
        // 응답에 포함할 데이터를 저장할 Map 객체 생성
        Map<String, Object> response = new HashMap<>();

        // 실패 상태 및 예외 메시지 설정
        response.put("status", "failure");
        response.put("message", ex.getMessage());

        // HTTP 400 (Bad Request) 상태 코드로 응답 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // InvalidAccountException 발생 시 처리하는 메소드
    @ExceptionHandler(InvalidAccountException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAccountException(InvalidAccountException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failure");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // HTTP 400 (Bad Request)
    }

    // TransferException 발생 시 처리하는 메소드
    @ExceptionHandler(TransferException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTransferException(TransferException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failure");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // HTTP 400 (Bad Request)
    }

    // 위에 명시되지 않은 다른 예외가 발생할 때 처리하는 메소드
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> response = new HashMap<>();

        // 일반적인 오류 메시지와 실패 상태 설정
        response.put("status", "failure");
        response.put("message", "알 수 없는 오류가 발생했습니다.");

        // HTTP 500 (Internal Server Error) 상태 코드로 응답 반환
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
