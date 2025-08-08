package kr.co.yeogiga.domain.trip.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.yeogiga.domain.trip.dto.TripDto;
import kr.co.yeogiga.domain.trip.entity.QTrip;
import kr.co.yeogiga.domain.trip.entity.QTripMember;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomTripMemberRepositoryImpl implements CustomTripMemberRepository {
    private final JPAQueryFactory jpaQueryFactory;
    
    private final QTripMember tripMember = QTripMember.tripMember;
    private final QTrip trip = QTrip.trip;
    private final QUser user = QUser.user;
    
    @Override
    public Optional<TripDto.Summary> findTripSummaryByTripId(Long tripId) {
        Trip tripEntity = jpaQueryFactory
                .select(trip)
                .from(trip)
                .where(trip.id.eq(tripId))
                .fetchOne();
        
        if (Objects.isNull(tripEntity)) {
            return Optional.empty();
        }
        
        List<TripDto.MemberInfo> members = jpaQueryFactory
                .select(
                        Projections.constructor(
                                TripDto.MemberInfo.class,
                                user.id,
                                user.nickname,
                                user.imageUrl
                        )
                )
                .from(tripMember)
                .join(tripMember.user, user)
                .where(tripMember.trip.id.eq(tripId))
                .fetch();
        
        return Optional.of(TripDto.Summary.from(tripEntity, members));
    }
    
    @Override
    public List<TripDto.Summary> findAllTripSummaryByUserId(Long userId, TravelStatus status) {
        List<Trip> tripList = jpaQueryFactory
                .select(trip)
                .from(tripMember)
                .join(tripMember.trip, trip)
                .where(
                        tripMember.user.id.eq(userId),
                        eqTravelStatus(status)
                )
                .fetch();
        
        if (tripList.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> tripIds = tripList.stream()
                .map(Trip::getId)
                .toList();
        
        List<Tuple> userList = jpaQueryFactory
                .select(
                        trip.id,
                        user.id,
                        user.imageUrl,
                        user.nickname
                )
                .from(tripMember)
                .join(tripMember.trip, trip)
                .join(tripMember.user, user)
                .where(
                        tripMember.trip.id.in(tripIds)
                )
                .fetch();
        
        Map<Long, List<TripDto.MemberInfo>> memberInfoMap = userList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(trip.id),
                        Collectors.mapping(tuple -> TripDto.MemberInfo.builder()
                                .userId(tuple.get(user.id))
                                .nickname(tuple.get(user.nickname))
                                .imageUrl(tuple.get(user.imageUrl))
                                .build(), Collectors.toList()
                        )
                ));
        
        return tripList.stream()
                .map(trip -> TripDto.Summary.from(
                        trip,
                        memberInfoMap.getOrDefault(trip.getId(), List.of()))
                ).toList();
    }
    
    /**
     * 여행 상태(TravelStatus) 일치 여부 조건 BooleanExpression 반환 메서드
     *
     * @param travelStatus  여행 상태
     * @return              여행 상태 일치 여부 조건문
     */
    private BooleanExpression eqTravelStatus(TravelStatus travelStatus) {
        return travelStatus == null ? null : trip.travelStatus.eq(travelStatus);
    }
}
