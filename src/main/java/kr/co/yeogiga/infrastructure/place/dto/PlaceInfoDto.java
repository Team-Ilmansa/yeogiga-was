package kr.co.yeogiga.infrastructure.place.dto;

import lombok.Getter;

@Getter
public abstract class PlaceInfoDto {
    protected String title;
    protected String link;
    protected String description;
    protected String telephone;
    protected String address;
    protected String roadAddress;
    protected double longitude;
    protected double latitude;
}
