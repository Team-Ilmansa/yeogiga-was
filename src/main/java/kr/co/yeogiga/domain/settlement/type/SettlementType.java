package kr.co.yeogiga.domain.settlement.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SettlementType {
    RESTAURANT("식당"),
    TOURISM("관광지"),
    LODGING("숙소"),
    TRANSPORT("교통수단"),
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
