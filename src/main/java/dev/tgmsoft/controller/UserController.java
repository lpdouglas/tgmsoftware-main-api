package dev.tgmsoft.controller;

import dev.tgmsoft.model.UserModel;
import dev.tgmsoft.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

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
            
            // Get count of documents
            long count = mongoTemplate.count(null, UserModel.class);
            logger.info("Total documents in 'users' collection: {}", count);
            
            // Query all users
            List<UserModel> result = userRepository.findAll();
            
            logger.info("Successfully retrieved {} users from database", result.size());
            
            if (result.isEmpty()) {
                logger.warn("⚠️ No users found in database. Collection might be empty or not exist.");
                logger.warn("Expected collection name: 'users'");
                logger.warn("Expected database: 'sample_mflix'");
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

