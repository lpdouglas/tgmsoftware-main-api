package dev.tgmsoft.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Component
public class MongoDbConfig {
    // MongoDB configuration loaded from environment variables
}

@Configuration
class MongoClientConfigurer extends AbstractMongoClientConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MongoClientConfigurer.class);

    @Autowired
    private Environment env;

    @Bean(name = "mongoClient")
    @ConditionalOnMissingBean
    public MongoClient mongoClient() {
        String username = env.getProperty("MONGODB_USERNAME");
        String password = env.getProperty("MONGODB_PASSWORD");
        
        String connectionString = String.format(
            "mongodb+srv://%s:%s@tgmsoftware.kbnb6qc.mongodb.net/sample_mflix?appName=sample_mflix&retryWrites=true&w=majority&tls=true",
            username,
            password
        );
        
        try {
            MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
            return MongoClients.create(settings);
        } catch (Exception e) {
            logger.error("Failed to create MongoDB client", e);
            throw new RuntimeException("Failed to create MongoDB client with provided credentials", e);
        }
    }

    @Override
    protected String getDatabaseName() {
        return "sample_mflix";
    }
}
