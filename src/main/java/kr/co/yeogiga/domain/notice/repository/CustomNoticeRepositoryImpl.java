package kr.co.yeogiga.domain.notice.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.yeogiga.domain.notice.dto.NoticeDto;
import kr.co.yeogiga.domain.notice.entity.Notice;
import kr.co.yeogiga.domain.notice.entity.QNotice;
import kr.co.yeogiga.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomNoticeRepositoryImpl implements CustomNoticeRepository {
    private final JPAQueryFactory jpaQueryFactory;
    
    private final QNotice notice = QNotice.notice;
    private final QUser user = QUser.user;
    
    @Override
    public Optional<Long> findAuthorIdById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(notice.authorId)
                        .from(notice)
                        .where(notice.id.eq(id))
                        .fetchFirst()
        );
    }
    
    @Override
    public Optional<Notice> findNoticeJoinUser(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(notice)
                        .join(notice.author, user)
                        .where(notice.id.eq(id))
                        .fetchFirst()
        );
    }
    
    @Override
    public Page<NoticeDto.Detail> findAllNoticeDetailByTripId(Long tripId, Pageable pageable) {
        List<NoticeDto.Detail> noticeDetails = jpaQueryFactory.
                select(
                        Projections.constructor(
                                NoticeDto.Detail.class,
                                notice.id,
                                notice.title,
                                notice.description,
                                notice.createdAt,
                                user.id,
                                user.nickname,
                                user.imageUrl
                        )
                )
                .from(notice)
                .join(notice.author, user)
                .where(notice.tripId.eq(tripId))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        JPAQuery<Long> count = jpaQueryFactory
                .select(notice.count())
                .from(notice)
                .where(notice.tripId.eq(tripId));
        
        return PageableExecutionUtils.getPage(noticeDetails, pageable, count::fetchOne);
    }
    
    /**
     * 공지사항(notice) 엔티티 동적 정렬 설정 메서드
     * - Pageable 객체에 정렬값 미설정 시, 기본적으로 생성날짜 내림차순 정렬
     *
     * @param pageable  페이지네이션 객체
     * @return          정렬 기준 객체 배열
     */
    private OrderSpecifier[] getOrderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{new OrderSpecifier(Order.DESC, notice.createdAt)};
        }
        
        return pageable.getSort().stream().map(sort -> {
            Order order = sort.isAscending() ? Order.ASC : Order.DESC;
            String property = sort.getProperty();
            Path<Object> target = Expressions.path(Object.class, notice, property);
            return new OrderSpecifier(order, target);
        }).toArray(OrderSpecifier[]::new);
    }
}
