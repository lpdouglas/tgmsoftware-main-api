package dev.tgmsoft.controller;

import dev.tgmsoft.model.UserModel;
import dev.tgmsoft.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    UserRepository userRepository;

    @GetMapping("/users")
    public List<UserModel> users() {
        logger.info("=== GET /users endpoint called ===");
        logger.info("Attempting to connect to MongoDB...");
        try {
            List<UserModel> result = userRepository.findAll();
            logger.info("Successfully retrieved {} users from database", result.size());
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

