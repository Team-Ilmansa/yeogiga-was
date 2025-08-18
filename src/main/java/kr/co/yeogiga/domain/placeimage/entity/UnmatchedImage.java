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
@Document(collection = "unmatched_image")
public class UnmatchedImage {
    @Id
    private String id;
    private Long tripId;
    private int day;
    private List<Image> images;

    @Builder
    public UnmatchedImage(Long tripId, int day) {
        this.images = new ArrayList<>();
        this.tripId = tripId;
        this.day = day;
    }

}
