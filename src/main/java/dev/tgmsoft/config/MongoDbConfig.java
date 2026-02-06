package dev.tgmsoft.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class MongoDbConfig {

    private static final Logger logger = LoggerFactory.getLogger(MongoDbConfig.class);

    @Autowired
    private Environment env;

    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        logger.info("========== MONGODB CONFIGURATION DETAILED DEBUG ==========");
        
        String mongoUri = env.getProperty("spring.data.mongodb.uri");
        String username = env.getProperty("MONGODB_USERNAME");
        String password = env.getProperty("MONGODB_PASSWORD");
        
        // Detailed checks
        logger.info("MongoDB Connection Details:");
        logger.info("  - Username set: {}", username != null && !username.isEmpty());
        logger.info("  - Password set: {}", password != null && !password.isEmpty());
        
        if (username != null) {
            logger.info("  - Username starts with: {}", username.substring(0, Math.min(5, username.length())));
            logger.info("  - Username length: {}", username.length());
        }
        
        if (password != null) {
            logger.info("  - Password length: {}", password.length());
            // Check for special characters that need URL encoding
            if (password.contains("@")) logger.warn("    ! Password contains @ - may cause URI parsing issues");
            if (password.contains(":")) logger.warn("    ! Password contains : - may cause URI parsing issues");
            if (password.contains("/")) logger.warn("    ! Password contains / - may cause URI parsing issues");
            if (password.contains("?")) logger.warn("    ! Password contains ? - may cause URI parsing issues");
            if (password.contains("#")) logger.warn("    ! Password contains # - may cause URI parsing issues");
        }
        
        logger.info("  - URI is null: {}", mongoUri == null);
        logger.info("  - URI is empty: {}", mongoUri != null && mongoUri.isEmpty());
        
        if (mongoUri != null && !mongoUri.isEmpty()) {
            // Check if variables are interpolated
            if (mongoUri.contains("${")) {
                logger.error("  ! CRITICAL: MongoDB URI contains unresolved variables!");
                logger.error("    Raw URI: {}", mongoUri);
            } else {
                logger.info("  - URI variables are resolved");
                // Safe to show the connection string structure
                if (mongoUri.contains("mongodb+srv://")) {
                    logger.info("  - Using MongoDB Atlas (SRV)");
                }
            }
            
            // Extract host if possible
            try {
                if (mongoUri.contains("@")) {
                    String hostPart = mongoUri.substring(mongoUri.indexOf("@") + 1);
                    String host = hostPart.split("/")[0];
                    logger.info("  - Connection hosts: {}", host);
                }
            } catch (Exception e) {
                logger.warn("  - Could not parse URI for host information");
            }
        }
        
        logger.info("SSL/TLS Debug Info:");
        logger.info("  - Java version: {}", System.getProperty("java.version"));
        logger.info("  - Java vendor: {}", System.getProperty("java.vendor"));
        logger.info("  - OS: {}", System.getProperty("os.name"));
        
        // Check system SSL properties 
        boolean sslDebug = Boolean.parseBoolean(System.getProperty("javax.net.debug", "false"));
        logger.info("  - javax.net.debug enabled: {}", sslDebug);
        
        logger.info("========================================================");
    }
}
