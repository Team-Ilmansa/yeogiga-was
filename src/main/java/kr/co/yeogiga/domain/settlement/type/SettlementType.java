package kr.co.yeogiga.domain.settlement.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SettlementType {
    ATTRACTION("관광지"),
    LODGING("숙소"),
    MEAL("식사"),
    TRANSPORTATION("이동수단"),
    ETC("기타");
    
    private final String value;
    
    @JsonCreator
    public static SettlementType parse(String input) {
        return Arrays.stream(values())
                .filter(type -> type.toString().equals(input))
                .findFirst()
                .orElse(null);
    }
}
