package kr.co.yeogiga.infrastructure.config;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLTemplates;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }
    
    @Bean
    public SQLTemplates sqlTemplates()  {
        return new MySQLTemplates();
    }
}
