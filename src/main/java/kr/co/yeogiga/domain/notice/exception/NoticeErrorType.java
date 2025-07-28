package kr.co.yeogiga.domain.notice.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum NoticeErrorType implements BaseErrorType {
    NOT_FOUND(HttpStatus.NOT_FOUND, "N000", "존재하지 않는 공지사항입니다."),
    UNAUTHORIZED_AUTHOR(HttpStatus.FORBIDDEN, "N001", "공지사항의 작성자가 아닙니다.")
    ;
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    
    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
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
