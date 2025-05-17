package kr.co.yeogiga.application.route.dto;

import kr.co.yeogiga.domain.triproute.entity.Route;
import kr.co.yeogiga.domain.triproute.entity.TripRoute;

import java.util.List;

public record RouteRes(
        int day,
        List<Route> routes
) {
    public static RouteRes from(TripRoute tripRoute) {
        return new RouteRes(tripRoute.getDay(), tripRoute.getRoutes());
    }
}
