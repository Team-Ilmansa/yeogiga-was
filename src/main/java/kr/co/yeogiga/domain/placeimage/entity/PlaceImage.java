package kr.co.yeogiga.domain.placeimage.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "place_image")
public class PlaceImage {
    @Id
    private String id;
    private Long placeId;
    private List<Image> images;

    @Builder
    public PlaceImage(Long placeId) {
        this.images = new ArrayList<>();
        this.placeId = placeId;
    }

}
