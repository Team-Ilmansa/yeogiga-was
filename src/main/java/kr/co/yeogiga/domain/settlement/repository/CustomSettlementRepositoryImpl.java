package kr.co.yeogiga.domain.settlement.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.yeogiga.domain.settlement.dto.PayInfoDto;
import kr.co.yeogiga.domain.settlement.dto.SettlementDto;
import kr.co.yeogiga.domain.settlement.entity.QPayInfo;
import kr.co.yeogiga.domain.settlement.entity.QSettlement;
import kr.co.yeogiga.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomSettlementRepositoryImpl implements CustomSettlementRepository {
    private final JPAQueryFactory jpaQueryFactory;
    
    private final QSettlement settlement = QSettlement.settlement;
    private final QPayInfo payInfo = QPayInfo.payInfo;
    private final QUser user = QUser.user;
    
    @Override
    public Optional<Long> findPayerIdById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(settlement.payerId)
                        .from(settlement)
                        .where(settlement.id.eq(id))
                        .fetchFirst()
        );
    }
    
    @Override
    public Optional<SettlementDto> findSettlementDtoById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .from(settlement)
                        .join(payInfo).on(settlement.id.eq(payInfo.settlementId))
                        .join(user).on(payInfo.userId.eq(user.id))
                        .where(settlement.id.eq(id))
                        .transform(
                                GroupBy.groupBy(settlement.id).as(
                                        Projections.constructor(
                                                SettlementDto.class,
                                                settlement.id,
                                                settlement.name,
                                                settlement.totalPrice,
                                                settlement.date,
                                                settlement.type,
                                                settlement.payerId,
                                                settlement.isCompleted,
                                                GroupBy.list(
                                                        Projections.constructor(
                                                                PayInfoDto.class,
                                                                payInfo.id,
                                                                payInfo.userId,
                                                                user.nickname,
                                                                user.imageUrl,
                                                                payInfo.price,
                                                                payInfo.isCompleted
                                                        )
                                                )
                                        )
                                )
                        ).get(id)
        );
    }
    
    @Override
    public List<SettlementDto> findAllSettlementDtoByTripId(Long tripId) {
        Collection<SettlementDto> result = jpaQueryFactory
                .from(settlement)
                .where(settlement.tripId.eq(tripId))
                .join(payInfo).on(settlement.id.eq(payInfo.settlementId))
                .join(user).on(payInfo.userId.eq(user.id))
                .transform(
                        GroupBy.groupBy(settlement.id).as(
                                Projections.constructor(
                                        SettlementDto.class,
                                        settlement.id,
                                        settlement.name,
                                        settlement.totalPrice,
                                        settlement.date,
                                        settlement.type,
                                        settlement.payerId,
                                        settlement.isCompleted,
                                        GroupBy.list(
                                                Projections.constructor(
                                                        PayInfoDto.class,
                                                        payInfo.id,
                                                        payInfo.userId,
                                                        user.nickname,
                                                        user.imageUrl,
                                                        payInfo.price,
                                                        payInfo.isCompleted
                                                )
                                        )
                                )
                        )
                ).values();

        return new ArrayList<>(result);
    }
}
