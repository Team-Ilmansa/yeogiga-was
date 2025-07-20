package kr.co.yeogiga.domain.trip.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.yeogiga.application.trip.dto.TripMemberRes;
import kr.co.yeogiga.application.trip.dto.TripRes;
import kr.co.yeogiga.domain.trip.entity.QTrip;
import kr.co.yeogiga.domain.trip.entity.QTripMember;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomTripMemberRepositoryImpl implements CustomTripMemberRepository {
    private final JPAQueryFactory jpaQueryFactory;
    
    private final QTripMember tripMember = QTripMember.tripMember;
    private final QTrip trip = QTrip.trip;
    private final QUser user = QUser.user;
    
    @Override
    public List<TripRes.TripSummary> findAllTripSummaryByUserId(Long userId) {
        List<Trip> tripList = jpaQueryFactory
                .select(trip)
                .from(tripMember)
                .join(tripMember.trip, trip)
                .where(tripMember.user.id.eq(userId))
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
        
        Map<Long, List<TripMemberRes.MemberInfo>> memberInfoMap = userList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(trip.id),
                        Collectors.mapping(tuple -> TripMemberRes.MemberInfo.builder()
                                .userId(tuple.get(user.id))
                                .nickname(tuple.get(user.nickname))
                                .imageUrl(tuple.get(user.imageUrl))
                                .build(), Collectors.toList()
                        )
                ));
        
        return tripList.stream()
                .map(trip -> TripRes.TripSummary.from(
                        trip,
                        memberInfoMap.getOrDefault(trip.getId(), List.of()))
                ).toList();
    }
}
