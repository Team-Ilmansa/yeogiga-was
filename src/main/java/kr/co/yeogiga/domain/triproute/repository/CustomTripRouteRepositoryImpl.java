package kr.co.yeogiga.domain.triproute.repository;

import kr.co.yeogiga.domain.triproute.converter.RouteListConverter;
import kr.co.yeogiga.domain.triproute.entity.TripRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
public class CustomTripRouteRepositoryImpl implements CustomTripRouteRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RouteListConverter routeListConverter;

    private final static int BATCH_SIZE = 100;

    @Override
    public void saveAllInBatch(List<TripRoute> tripRoutes) {
        String sql = "INSERT INTO trip_route (trip_id, day, routes) " +
                "VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                tripRoutes,
                BATCH_SIZE,
                (PreparedStatement ps, TripRoute tripRoute) -> {
                    ps.setLong(1, tripRoute.getTripId());
                    ps.setInt(2, tripRoute.getDay());
                    ps.setString(3, routeListConverter.convertToDatabaseColumn(tripRoute.getRoutes()));
                });
    }
}
