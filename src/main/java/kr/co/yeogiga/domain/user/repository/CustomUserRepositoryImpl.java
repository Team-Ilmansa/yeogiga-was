package kr.co.yeogiga.domain.user.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.SQLTemplates;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.yeogiga.domain.oauth.entity.OAuth;
import kr.co.yeogiga.domain.user.entity.QUser;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final SQLTemplates sqlTemplates;
    private final JPAQueryFactory jpaQueryFactory;
    
    @PersistenceContext
    private final EntityManager entityManager;
    
    private final QUser user = QUser.user;
    
    private final EntityPath<User> USER_ENTITY_PATH = new EntityPathBase<>(User.class, "users");
    private final EntityPath<OAuth> OAUTH_ENTITY_PATH = new EntityPathBase<>(OAuth.class, "oauth");
    
    private static final class USER_COLUMN {
        private USER_COLUMN() { }
        
        private static final String ID = "id";
        private static final String NICKNAME = "nickname";
        private static final String USERNAME = "username";
        private static final String DELETED_AT = "deleted_at";
        private static final String EMAIL = "email";
    }
    
    private static final class OAUTH_COLUMN {
        private OAUTH_COLUMN() { }
        
        private static final String USER = "user_id";
        private static final String PLATFORM = "platform";
        private static final String PLATFORM_ID = "platform_id";
    }
    
    @Override
    public Optional<User> findUserIncludeDeletedByEmail(String email) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        return Optional.ofNullable(
                jpaSqlQuery
                        .select(USER_ENTITY_PATH)
                        .from(USER_ENTITY_PATH)
                        .where(Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.EMAIL).eq(email))
                        .fetchFirst()
        );
    }
    
    @Override
    public Optional<User> findUserIncludeDeletedByPlatformAndPlatformId(String platform, String platformId) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        return Optional.ofNullable(
                jpaSqlQuery
                        .select(USER_ENTITY_PATH)
                        .from(USER_ENTITY_PATH)
                        .innerJoin(OAUTH_ENTITY_PATH)
                            .on(Expressions.numberPath(Long.class, USER_ENTITY_PATH, USER_COLUMN.ID)
                                    .eq(Expressions.numberPath(Long.class, OAUTH_ENTITY_PATH, OAUTH_COLUMN.USER)))
                        .where(
                                Expressions.stringPath(OAUTH_ENTITY_PATH, OAUTH_COLUMN.PLATFORM)
                                        .eq(platform),
                                Expressions.stringPath(OAUTH_ENTITY_PATH, OAUTH_COLUMN.PLATFORM_ID)
                                        .eq(platformId)
                        )
                        .fetchOne()
        );
    }
    
    @Override
    public Optional<User> findUserIncludeDeletedById(Long id) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        return Optional.ofNullable(
                jpaSqlQuery
                        .select(USER_ENTITY_PATH)
                        .from(USER_ENTITY_PATH)
                        .where(
                                Expressions.numberPath(Long.class, USER_ENTITY_PATH, USER_COLUMN.ID)
                                        .eq(id)
                        )
                        .fetchOne()
        );
    }
    
    @Override
    public Optional<User> findUserIncludeDeletedByNickname(String nickname) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        return Optional.ofNullable(
                jpaSqlQuery
                        .select(USER_ENTITY_PATH)
                        .from(USER_ENTITY_PATH)
                        .where(
                                Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.NICKNAME)
                                        .eq(nickname)
                        )
                        .fetchOne()
        );
    }
    
    @Override
    public Optional<User> findUserIncludeDeletedByUsername(String username) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        return Optional.ofNullable(
            jpaSqlQuery
                    .select(USER_ENTITY_PATH)
                    .from(USER_ENTITY_PATH)
                    .where(
                            Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.USERNAME)
                                    .eq(username)
                    )
                    .fetchOne()
        );
    }
    
    @Override
    public Optional<User> findUserIncludeDeletedByEmailAndUsername(String email, String username) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        return Optional.ofNullable(
                jpaSqlQuery
                        .select(USER_ENTITY_PATH)
                        .from(USER_ENTITY_PATH)
                        .where(
                                Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.EMAIL).eq(email),
                                Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.USERNAME).eq(username)
                        )
                        .fetchOne()
        );
    }
    
    @Override
    public List<Long> findDeletedUserIdBefore(LocalDate date) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        return jpaSqlQuery
                .select(
                        Expressions.numberPath(Long.class, USER_ENTITY_PATH, USER_COLUMN.ID)
                )
                .from(USER_ENTITY_PATH)
                .where(
                        Expressions.datePath(LocalDate.class, USER_ENTITY_PATH, USER_COLUMN.DELETED_AT).isNotNull(),
                        Expressions.datePath(LocalDate.class, USER_ENTITY_PATH, USER_COLUMN.DELETED_AT).loe(date)
                        
                )
                .fetch();
    }
    
    @Override
    public boolean existsIncludeDeletedByUsername(String username) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        Integer fetchOne = jpaSqlQuery
                .select(Expressions.ONE)
                .from(USER_ENTITY_PATH)
                .where(
                        Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.USERNAME)
                                .eq(username)
                )
                .fetchFirst();
        
        return fetchOne != null;
    }
    
    @Override
    public boolean existsIncludeDeletedByNickname(String nickname) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        Integer fetchOne = jpaSqlQuery
                .select(Expressions.ONE)
                .from(USER_ENTITY_PATH)
                .where(
                        Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.NICKNAME)
                                .eq(nickname)
                )
                .fetchFirst();
        
        return fetchOne != null;
    }
    
    @Override
    public boolean existsIdIncludeDeletedByEmail(String email) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        Integer fetchOne = jpaSqlQuery
                .select(Expressions.ONE)
                .from(USER_ENTITY_PATH)
                .where(
                        Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.EMAIL)
                                .eq(email)
                )
                .fetchFirst();
        
        return fetchOne != null;
    }
    
    @Override
    public boolean existsIncludeDeletedByEmailAndUsername(String email, String username) {
        JPASQLQuery<Tuple> jpaSqlQuery = new JPASQLQuery<>(entityManager, sqlTemplates);
        
        Integer fetchOne = jpaSqlQuery
                .select(Expressions.ONE)
                .from(USER_ENTITY_PATH)
                .where(
                        Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.EMAIL).eq(email),
                        Expressions.stringPath(USER_ENTITY_PATH, USER_COLUMN.USERNAME).eq(username)
                )
                .fetchFirst();
        
        return fetchOne != null;
    }
    
    @Override
    public void deleteHardAllByIdIn(List<Long> ids) {
        jpaQueryFactory
                .delete(user)
                .where(user.id.in(ids)).execute();
    }
}
