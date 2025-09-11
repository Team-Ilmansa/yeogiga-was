package kr.co.yeogiga.domain.trip.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceCategory {

    RESTAURANT("식당"),
    TOURISM("관광지"),
    LODGING("숙소"),
    TRANSPORT("교통수단"),
    ETC("기타");

    private final String label;

}

