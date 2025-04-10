package kr.co.yeogiga.common.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        Map<String, String> errors
) {
    public static ErrorResponse from(BaseErrorType error) {
        return ErrorResponse.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }
}
