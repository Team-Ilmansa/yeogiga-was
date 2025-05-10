package kr.co.yeogiga.domain.user.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * User ErrorCode: Uxxx
 */
@RequiredArgsConstructor
public enum UserErrorType implements BaseErrorType {
    NOT_FOUND(HttpStatus.NOT_FOUND, "U000", "존재하지 않는 사용자입니다."),
    ALREADY_EXIST_USERNAME(HttpStatus.CONFLICT, "U001", "이미 존재하는 아이디입니다."),
    SAME_PASSWORD(HttpStatus.CONFLICT, "U002", "기존과 동일한 비밀번호입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 불일치합니다."),
    ALREADY_WITHDRAW(HttpStatus.BAD_REQUEST, "U004", "이미 회원탈퇴한 사용자입니다.");



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
