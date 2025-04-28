package kr.co.yeogiga.common.response.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SuccessResponse<T>(
        int code,
        String message,
        T data
) {
    public static SuccessResponse<?> ok() {
        return SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .message("요청이 성공하였습니다.")
                .build();
    }

    public static SuccessResponse<?> created() {
        return SuccessResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("요청이 성공하였습니다.")
                .build();
    }

    public static <T> SuccessResponse<?> from(T data) {
        return SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .message("요청이 성공하였습니다.")
                .data(data)
                .build();
    }
}