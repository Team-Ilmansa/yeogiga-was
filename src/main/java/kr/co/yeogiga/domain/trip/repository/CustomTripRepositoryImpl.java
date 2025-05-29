package kr.co.yeogiga.domain.trip.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.yeogiga.domain.trip.dto.TripFcmTokenQueryDto;
import kr.co.yeogiga.domain.trip.entity.QTrip;
import kr.co.yeogiga.domain.trip.entity.QTripMember;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomTripRepositoryImpl implements CustomTripRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private final QTrip trip = QTrip.trip;
    private final QTripMember tripMember = QTripMember.tripMember;
    private final QUser user = QUser.user;

    @Override
    public List<TripFcmTokenQueryDto> findTripFcmTokensByTime(LocalDateTime time) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        TripFcmTokenQueryDto.class,
                        trip.id,
                        user.fcmToken,
                        trip.endedAt
                ))
                .from(tripMember)
                .join(tripMember.trip, trip)
                .join(tripMember.user, user)
                .where(
                        trip.travelStatus.ne(TravelStatus.IN_PROGRESS),
                        trip.travelStatus.ne(TravelStatus.SETTING),
                        trip.startedAt.loe(time),
                        trip.endedAt.goe(time)
                )
                .fetch();
    }
}
