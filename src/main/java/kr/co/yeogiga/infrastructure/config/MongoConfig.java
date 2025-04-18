package kr.co.yeogiga.infrastructure.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import kr.co.yeogiga.infrastructure.properties.MongoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@RequiredArgsConstructor
@EnableMongoAuditing
public class MongoConfig extends AbstractMongoClientConfiguration {
    private final MongoProperties mongoProperties;

    @Bean
    @Override
    public MongoClient mongoClient(){
        return MongoClients.create(mongoProperties.getConnectionUri());
    }

    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }

}
