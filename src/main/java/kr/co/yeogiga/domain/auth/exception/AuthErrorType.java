package kr.co.yeogiga.domain.auth.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Auth ErrorCode: Axxx
 */
@RequiredArgsConstructor
public enum AuthErrorType implements BaseErrorType {
    FORBIDDEN(HttpStatus.FORBIDDEN, "A000", "접근 권한이 없습니다."),
    UN_REGISTERED_USER(HttpStatus.FORBIDDEN, "A001", "회원가입이 필요한 사용자 입니다."),
    NOT_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "A003", "인증에 실패하였습니다. 토큰을 확인해주세요."),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "잘못된 토큰입니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "A006", "토큰 서명이 잘못되었습니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "A007", "토큰 인증 과정 중 에러가 발생하였습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.NOT_FOUND, "A008", "리프레시 토큰이 만료되었습니다. 재로그인 해주세요."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "A009", "리프레시 토큰을 찾을 수 없습니다."),

    AUTHENTICATION_FAIL(HttpStatus.BAD_REQUEST, "A010", "로그인에 실패하였습니다. 아이디 또는 비밀번호를 확인해주세요."),

    ALREADY_USED_USERNAME(HttpStatus.CONFLICT, "A011", "이미 사용 중인 아이디입니다."),
    ALREADY_USED_NICKNAME(HttpStatus.CONFLICT, "A012", "이미 사용 중인 닉네임입니다."),
    ALREADY_USED_EMAIL(HttpStatus.CONFLICT, "A013", "이미 사용 중인 이메일입니다."),
    
    EMAIL_VERIFICATION_TIMEOUT(HttpStatus.BAD_REQUEST, "A014", "이메일 인증 시간을 초과하였습니다."),
    EMAIL_VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "A015", "이메일 인증 번호가 일치하지 않습니다."),
    EMAIL_VERIFICATION_TIME_LIMIT(HttpStatus.BAD_REQUEST, "A016", "이메일 인증 시도 횟수를 초과하였습니다. 잠시 후 시도해주세요."),
    
    NOT_WITHDRAWN(HttpStatus.BAD_REQUEST, "A017", "탈퇴한 사용자가 아닙니다.");


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