package kr.co.yeogiga.domain.trip.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * TripMember ErrorCode: T1xx
 */
@RequiredArgsConstructor
public enum TripMemberErrorType implements BaseErrorType {

    ALREADY_EXISTS(HttpStatus.CONFLICT, "T100", "이미 여행에 참가 중인 사용자입니다."),
    LEADER_CAN_NOT_LEAVE_TRIP(HttpStatus.BAD_REQUEST, "T101", "방장은 여행에서 떠날 수 없습니다. 권한을 위임해주세요."),
    IS_NOT_MEMBER(HttpStatus.BAD_REQUEST, "T102", "해당 여행의 멤버가 아닙니다."),
    CAN_NOT_SELF_KICK(HttpStatus.BAD_REQUEST, "T103", "자기 자신은 추방할 수 없습니다."),
    ONLY_LEADER(HttpStatus.FORBIDDEN, "T104", "여행 방장만 이용 가능한 기능입니다."),
    EXISTS_NOT_MEMBER(HttpStatus.BAD_REQUEST, "T105", "여행 멤버가 아닌 사용자가 존재합니다.");

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
