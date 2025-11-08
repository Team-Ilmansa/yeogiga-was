package kr.co.yeogiga.domain.settlement.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Settlement ErrorCode: S0XX
 */
@RequiredArgsConstructor
public enum SettlementErrorType implements BaseErrorType {
    NOT_VALID_PRICE(HttpStatus.BAD_REQUEST, "S000", "정산 내역 금액 총합이 일치하지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "존재하지 않는 정산 내역 입니다."),
    IS_NOT_PAYER(HttpStatus.FORBIDDEN, "S002", "정산 생성자가 아닙니다."),
    PAY_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "S003", "존재하지 않는 분담 내역입니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
    
    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
