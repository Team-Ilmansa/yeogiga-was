package kr.co.yeogiga.common.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final BaseErrorType errorType;
}