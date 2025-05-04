package kr.co.yeogiga.domain.trip.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Trip ErrorCode: Txxx
 */
@RequiredArgsConstructor
public enum TripErrorType implements BaseErrorType {

    INVALID_PLACE(HttpStatus.BAD_REQUEST, "T001", "지원하지 않는 카테고리입니다."),
    ALREADY_ADDED_PLACE(HttpStatus.CONFLICT, "T002", "이미 추가된 장소입니다."),
    DAY_PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "T003", "해당 여행 일차 정보가 존재하지 않습니다."),
    NOT_FOUND_TEMP_PLACE(HttpStatus.NOT_FOUND, "T003", "임시 저장소에 해당 장소가 존재하지 않습니다.")
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
