package mobile.doan.supertodolist.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mobile.doan.supertodolist.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An error occurred")
                .error(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<String>> handleAppException(AppException e) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Bad Request")
                .error(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
