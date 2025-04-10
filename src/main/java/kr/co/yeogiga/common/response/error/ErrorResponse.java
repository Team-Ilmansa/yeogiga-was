package kr.co.yeogiga.common.response.error;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.Builder;

@Builder
public record ErrorResponse(
        String code,
        String message
) {
    public static ErrorResponse from(BaseErrorType error) {
        return ErrorResponse.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }
}
