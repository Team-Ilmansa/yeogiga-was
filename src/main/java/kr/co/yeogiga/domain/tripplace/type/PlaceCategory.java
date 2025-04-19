package kr.co.yeogiga.domain.tripplace.type;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum PlaceCategory {

    RESTAURANT("음식점", CategoryGroup.식당),
    CAFE("카페", CategoryGroup.식당),

    TOURIST_SPOT("관광명소", CategoryGroup.관광지),
    CULTURE_FACILITY("문화시설", CategoryGroup.관광지),

    LODGING("숙박", CategoryGroup.숙소),

    ETC("기타", CategoryGroup.기타)
    ;

    private final String label;
    private final CategoryGroup group;

    private static final Map<String, PlaceCategory> LABEL_MAP = new HashMap<>();

    static {
        for (PlaceCategory type : values()) {
            LABEL_MAP.put(type.label, type);
        }
    }

    /**
     * 전달된 문자열 라벨에 해당하는 PlaceCategory enum을 반환
     *
     * @param label : 클라이언트가 전달한 키워드 문자열
     * @return : 해당 키워드에 대응하는 PlaceCategory enum
     * @throws CustomException INVALID_PLACE : 유효하지 않은 장소일 경우 예외 발생
     */
    public static PlaceCategory fromLabel(String label) {
        PlaceCategory placeCategory = LABEL_MAP.get(label);
        if (placeCategory == null) {
            throw new CustomException(TripErrorType.INVALID_PLACE);
        }
        return placeCategory;
    }

    public String getGroupName() {
        return group.name();
    }

    enum CategoryGroup {
        식당, 관광지, 숙소, 기타
    }
}

