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
    TRIP_PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "T003", "해당 여행 일차 정보가 존재하지 않습니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "T005", "해당 목적지가 존재하지 않습니다"),

    TRIP_NOT_FOUND(HttpStatus.NOT_FOUND, "T006", "해당 여행이 존재하지 않습니다."),
    PERMISSION_DENIED_NOT_LEADER(HttpStatus.FORBIDDEN, "T007", "여행 방장이 아닙니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "T008", "여행 시작 시간과 종료 시간을 확인하세요."),

    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "T009", "사용자의 가능한 날짜 정보가 존재하지 않습니다."),
    CALENDAR_ALREADY_EXISTS(HttpStatus.CONFLICT, "T010", "이미 가능한 날짜 정보가 등록되어 있습니다.");

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
