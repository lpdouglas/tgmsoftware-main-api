package dev.tgmsoft.controller;

import dev.tgmsoft.model.UserModel;
import dev.tgmsoft.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @GetMapping("/users")
    public List<UserModel> users() {
        logger.info("=== GET /users endpoint called ===");
        logger.info("Attempting to query users from MongoDB...");
        
        try {
            // Check if collection exists
            boolean collectionExists = mongoTemplate.collectionExists(UserModel.class);
            logger.info("Collection 'users' exists: {}", collectionExists);
            
            if (!collectionExists) {
                // List all available collections for debugging
                Set<String> collections = mongoTemplate.getCollectionNames();
                logger.error("⚠️  The 'users' collection does NOT exist!");
                logger.error("Available collections in database: {}", collections);
                logger.error("Please create the 'users' collection or query a different collection");
                throw new RuntimeException("The 'users' collection does not exist in the database. Available collections: " + collections);
            }
            
            // Get count of documents using proper Query object
            long count = mongoTemplate.count(new Query(), UserModel.class);
            logger.info("Total documents in 'users' collection: {}", count);
            
            // Query all users
            List<UserModel> result = userRepository.findAll();
            
            logger.info("Successfully retrieved {} users from database", result.size());
            
            if (result.isEmpty()) {
                logger.warn("⚠️ No users found in database. Collection exists but is empty.");
            } else {
                for (int i = 0; i < Math.min(result.size(), 3); i++) {
                    UserModel user = result.get(i);
                    logger.info("  User {}: id={}, name={}, email={}", 
                        i + 1, user.getId(), user.getName(), user.getEmail());
                }
                if (result.size() > 3) {
                    logger.info("  ... and {} more users", result.size() - 3);
                }
            }
            
            return result;
        } catch (Exception e) {
            logger.error("ERROR connecting to MongoDB during /users request", e);
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Root cause: {} - {}", e.getCause().getClass().getName(), e.getCause().getMessage());
            }
            throw e;
        }
    }
}

