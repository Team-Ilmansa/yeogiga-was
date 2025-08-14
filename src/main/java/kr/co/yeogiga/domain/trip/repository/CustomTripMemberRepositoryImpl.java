package kr.co.yeogiga.domain.trip.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.yeogiga.domain.trip.dto.TripDto;
import kr.co.yeogiga.domain.trip.entity.QTrip;
import kr.co.yeogiga.domain.trip.entity.QTripMember;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

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
    public Page<TripDto.Summary> findAllTripSummaryByUserId(Long userId, TravelStatus status, Pageable pageable) {
        List<Trip> tripList = jpaQueryFactory
                .select(trip)
                .from(tripMember)
                .join(tripMember.trip, trip)
                .where(
                        tripMember.user.id.eq(userId),
                        eqTravelStatus(status)
                )
                .orderBy(getOrderSpecifiers(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        if (tripList.isEmpty()) {
            return Page.empty();
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
        
        List<TripDto.Summary> tripSummaries = tripList.stream()
                .map(trip -> TripDto.Summary.from(
                        trip,
                        memberInfoMap.getOrDefault(trip.getId(), List.of()))
                ).toList();
        
        JPAQuery<Long> count = jpaQueryFactory
                .select(trip.count())
                .from(tripMember)
                .join(tripMember.trip, trip)
                .where(
                        tripMember.user.id.eq(userId),
                        eqTravelStatus(status)
                );
        
        return PageableExecutionUtils.getPage(tripSummaries, pageable, count::fetchOne);
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
    
    /**
     * 여행 상태를 바탕으로 정렬 기준을 제공하는 메서드
     * - 전체: 시작 일자 내림차순, null 값 우선 배치
     * - SETTING, PLANNED, IN_PROGRESS: 시작 일자 오름차순, null 값 후순위 배치
     * - COMPLETED: 종료 일자 내림차순, null 값 후순위 배치
     *
     * @param travelStatus  여행 상태
     * @return              상태별 정렬 기준을 담은 OrderSpecifier 객체
     */
    private OrderSpecifier getOrderSpecifiers(TravelStatus travelStatus) {
        Path<Object> target = Expressions.path(Object.class, trip.startedAt.getMetadata().getName());
        
        if (travelStatus == null) {
            return new OrderSpecifier(Order.DESC, target, OrderSpecifier.NullHandling.NullsFirst);
        } else if (travelStatus == TravelStatus.SETTING || travelStatus == TravelStatus.PLANNED || travelStatus== TravelStatus.IN_PROGRESS) {
            return new OrderSpecifier(Order.ASC, target, OrderSpecifier.NullHandling.NullsLast);
        } else if (travelStatus == TravelStatus.COMPLETED) {
            target = Expressions.path(Object.class, trip, trip.endedAt.getMetadata().getName());
            return new OrderSpecifier(Order.DESC, target, OrderSpecifier.NullHandling.NullsLast);
        } else {
            return null;
        }
    }
}
