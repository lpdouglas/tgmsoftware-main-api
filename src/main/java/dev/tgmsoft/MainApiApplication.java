package dev.tgmsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class MainApiApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(MainApiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MainApiApplication.class, args);
	}
	
	@Autowired
	private Environment env;
	
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("========== CONFIGURATION DEBUG INFO ==========");
		
		String mongoUsername = env.getProperty("MONGODB_USERNAME");
		String mongoPassword = env.getProperty("MONGODB_PASSWORD");
		String mongoUri = env.getProperty("spring.data.mongodb.uri");
		
		// Log raw environment variables
		logger.info("Raw Environment Variables:");
		logger.info("  MONGODB_USERNAME: {}", 
			(mongoUsername != null && !mongoUsername.isEmpty()) ? "SET (length: " + mongoUsername.length() + ")" : "NOT SET OR EMPTY");
		logger.info("  MONGODB_PASSWORD: {}", 
			(mongoPassword != null && !mongoPassword.isEmpty()) ? "SET (length: " + mongoPassword.length() + ")" : "NOT SET OR EMPTY");
		
		// Log the constructed URI (masked for security)
		if (mongoUri != null && !mongoUri.isEmpty()) {
			String maskedUri = maskMongoUri(mongoUri);
			logger.info("  MongoDB URI: {}", maskedUri);
		} else {
			logger.warn("  MongoDB URI: NOT SET OR EMPTY - This will cause connection failures!");
		}
		
		// Check if username or password contain special characters that might not be properly encoded
		if (mongoUsername != null && mongoUsername.contains("@")) {
			logger.warn("MONGODB_USERNAME contains @ symbol - this may need URL encoding!");
		}
		if (mongoPassword != null && mongoPassword.contains("@")) {
			logger.warn("MONGODB_PASSWORD contains @ symbol - this may need URL encoding!");
		}
		if (mongoPassword != null && mongoPassword.contains(":")) {
			logger.warn("MONGODB_PASSWORD contains : symbol - this may need URL encoding!");
		}
		
		logger.info("Stripe Configuration:");
		String stripeKey = env.getProperty("STRIPE_SECRET_KEY");
		String stripeKeyFallback = env.getProperty("STRIPE_KEY");
		logger.info("  STRIPE_SECRET_KEY: {}", 
			(stripeKey != null && !stripeKey.isEmpty()) ? "SET (length: " + stripeKey.length() + ")" : "NOT SET");
		logger.info("  STRIPE_KEY (fallback): {}", 
			(stripeKeyFallback != null && !stripeKeyFallback.isEmpty()) ? "SET (length: " + stripeKeyFallback.length() + ")" : "NOT SET");
		
		logger.info("=========================================");
		logger.info("MongoDB Connection: Ready to connect on first request to /users");
	}
	
	/**
	 * Masks the MongoDB URI for logging purposes (hides password)
	 */
	private String maskMongoUri(String uri) {
		// Replace password portion with masked value
		return uri.replaceAll("mongodb\\+srv://[^:]+:([^@]+)@", "mongodb+srv://[username]:[PASSWORD]@");
	}
}
