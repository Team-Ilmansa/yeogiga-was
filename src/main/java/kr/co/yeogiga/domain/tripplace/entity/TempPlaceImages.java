package kr.co.yeogiga.domain.tripplace.entity;

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
@Document(collection = "temp_place_images")
public class TempPlaceImages {
    @Id
    private String id;
    private String tripDayPlaceId;
    private List<Image> images;

    @Builder
    public TempPlaceImages(String tripDayPlaceId) {
        this.tripDayPlaceId = tripDayPlaceId;
        this.images = new ArrayList<>();
    }

    public void addImage(Image image) {
        this.images.add(image);
    }
}
