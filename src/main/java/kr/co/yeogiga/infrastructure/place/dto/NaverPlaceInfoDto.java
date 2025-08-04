package kr.co.yeogiga.infrastructure.place.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class NaverPlaceInfoDto extends PlaceInfoDto {
    @JsonCreator
    public NaverPlaceInfoDto(
            String title,
            String link,
            String description,
            String telephone,
            String address,
            String roadAddress,
            @JsonProperty(value = "mapx") String mapX,
            @JsonProperty(value = "mapy") String mapY
    ) {
        this.title = title.replaceAll("<.*?>", "");
        this.link = link;
        this.description = description;
        this.telephone = telephone;
        this.address = address;
        this.roadAddress = roadAddress;
        this.longitude = Double.parseDouble(mapX.substring(0,3) + "." + mapX.substring(3));
        this.latitude = Double.parseDouble(mapY.substring(0,2) + "." + mapY.substring(2));
    }
}
