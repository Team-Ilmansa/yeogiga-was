package kr.co.yeogiga.common.exception;

import kr.co.yeogiga.common.response.error.ErrorResponse;
import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* CustomException 예외 처리 */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(final CustomException e) {
        BaseErrorType error = e.getErrorType();
        log.error("[Error Occurred] {}", error.getMessage());
        
        Object data = e.getData();
        
        if (Objects.nonNull(data)) {
            return ResponseEntity.status(error.getHttpStatus()).body(ErrorResponse.from(error, data));
        }
        
        return ResponseEntity.status(error.getHttpStatus()).body(ErrorResponse.from(error));
    }

    /* Argument Validation 예외 처리 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleValidationException(final MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.error("[Error Occurred] {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                .code(CommonErrorType.VALIDATION_ERROR.getCode())
                .errors(errors)
                .build());
    }

    /* Path Variable 예외 처리 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<?> handleValidationException(final MethodArgumentTypeMismatchException e) {
        BaseErrorType error = CommonErrorType.INTERNAL_SERVER_ERROR;;
        
        if (e.getParameter().hasParameterAnnotation(PathVariable.class)) {
            error = CommonErrorType.PATH_VARIABLE_VALIDATION_ERROR;
        } else if (e.getParameter().hasParameterAnnotation(RequestParam.class)) {
            error = CommonErrorType.QUERY_STRING_VALIDATION_ERROR;
        }
        
        log.error("[Error Occurred] {}", e.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(ErrorResponse.from(error));
    }

    /* 일반 예외 처리 */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(final Exception e) {
        log.error("[Error Occurred] {}", e.getMessage());
        BaseErrorType error = CommonErrorType.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(error.getHttpStatus()).body(ErrorResponse.from(error));
    }
}