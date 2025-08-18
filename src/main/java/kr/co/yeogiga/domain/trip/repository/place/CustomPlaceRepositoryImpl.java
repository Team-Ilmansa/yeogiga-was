package kr.co.yeogiga.domain.trip.repository.place;

import kr.co.yeogiga.domain.trip.entity.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
public class CustomPlaceRepositoryImpl implements CustomPlaceRepository {
    private final JdbcTemplate jdbcTemplate;

    private final static int BATCH_SIZE = 100;

    @Override
    public void saveAllInBatch(List<Place> places) {
        String sql = "INSERT INTO place ("
                + "name, latitude, longitude, sort_order, is_visited, place_type, trip_day_id"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                places,
                BATCH_SIZE,
                (PreparedStatement ps, Place place) -> {
                    ps.setString(1, place.getName());
                    ps.setDouble(2, place.getLatitude());
                    ps.setDouble(3, place.getLongitude());
                    ps.setInt(4, place.getSortOrder());
                    ps.setBoolean(5, place.isVisited());
                    ps.setString(6, place.getPlaceType().name());
                    ps.setLong(7, place.getTripDay().getId());
                }
        );
    }
}
