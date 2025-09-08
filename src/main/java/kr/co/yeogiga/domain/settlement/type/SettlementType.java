package kr.co.yeogiga.domain.settlement.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementType {
    ATTRACTION("관광지"),
    LODGING("숙소"),
    MEAL("식사"),
    TRANSPORTATION("이동수단"),
    ETC("기타");
    
    private final String value;
}
