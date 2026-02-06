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
		logger.info("========== APPLICATION STARTUP ==========");
		SpringApplication.run(MainApiApplication.class, args);
	}
	
	@Autowired
	private Environment env;
	
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("\n");
		logger.info("========== CONFIGURATION DEBUG INFO ==========");
		
		String mongoUsername = env.getProperty("MONGODB_USERNAME");
		String mongoPassword = env.getProperty("MONGODB_PASSWORD");
		String mongoUri = env.getProperty("spring.data.mongodb.uri");
		
		// Log raw environment variables
		logger.info("Raw Environment Variables:");
		logger.info("  MONGODB_USERNAME: {}", 
			(mongoUsername != null && !mongoUsername.isEmpty()) ? "SET (length: " + mongoUsername.length() + ")" : "❌ NOT SET OR EMPTY");
		logger.info("  MONGODB_PASSWORD: {}", 
			(mongoPassword != null && !mongoPassword.isEmpty()) ? "SET (length: " + mongoPassword.length() + ")" : "❌ NOT SET OR EMPTY");
		
		// CRITICAL: Log the ACTUAL URI Spring is using
		logger.info("\n⚠️  ACTUAL MongoDB Connection URI Being Used:");
		if (mongoUri != null && !mongoUri.isEmpty()) {
			// Check if it contains unresolved variables
			if (mongoUri.contains("${")) {
				logger.error("❌ CRITICAL: MongoDB URI contains UNRESOLVED variables!");
				logger.error("   Raw URI template: {}", mongoUri);
				logger.error("   This means environment variables are NOT being loaded!");
			} else if (mongoUri.contains("localhost")) {
				logger.error("❌ CRITICAL: MongoDB URI is using LOCALHOST!");
				logger.error("   URI: {}", mongoUri);
				logger.error("   This means environment variables are NOT being interpolated!");
				logger.error("   Expected to connect to: ac-6ttpd7u-shard-00-*.kbnb6qc.mongodb.net");
			} else {
				String maskedUri = maskMongoUri(mongoUri);
				logger.info("✓ URI looks correct: {}", maskedUri);
			}
		} else {
			logger.error("❌ CRITICAL: MongoDB URI is NULL or EMPTY!");
		}
		
		// Check if username or password contain special characters that might not be properly encoded
		if (mongoUsername != null && mongoUsername.contains("@")) {
			logger.warn("⚠️  MONGODB_USERNAME contains @ symbol - this may need URL encoding!");
		}
		if (mongoPassword != null && mongoPassword.contains("@")) {
			logger.warn("⚠️  MONGODB_PASSWORD contains @ symbol - this may need URL encoding!");
		}
		if (mongoPassword != null && mongoPassword.contains(":")) {
			logger.warn("⚠️  MONGODB_PASSWORD contains : symbol - this may need URL encoding!");
		}
		
		logger.info("\nStripe Configuration:");
		String stripeKey = env.getProperty("STRIPE_SECRET_KEY");
		String stripeKeyFallback = env.getProperty("STRIPE_KEY");
		logger.info("  STRIPE_SECRET_KEY: {}", 
			(stripeKey != null && !stripeKey.isEmpty()) ? "SET (length: " + stripeKey.length() + ")" : "NOT SET");
		logger.info("  STRIPE_KEY (fallback): {}", 
			(stripeKeyFallback != null && !stripeKeyFallback.isEmpty()) ? "SET (length: " + stripeKeyFallback.length() + ")" : "NOT SET");
		
		logger.info("\n========== END DEBUG INFO ==========\n");
		
		// DIAGNOSTIC: Show what went wrong
		if ((mongoUsername == null || mongoUsername.isEmpty()) || 
		    (mongoPassword == null || mongoPassword.isEmpty())) {
			logger.error("\n🔴 CONNECTION WILL FAIL - Environment Variables Not Set!");
			logger.error("On Render Host, make sure these environment variables are configured:");
			logger.error("  - MONGODB_USERNAME");
			logger.error("  - MONGODB_PASSWORD");
			logger.error("Check Render Dashboard → Service → Environment to add them.\n");
		}
	}
	
	/**
	 * Masks the MongoDB URI for logging purposes (hides password)
	 */
	private String maskMongoUri(String uri) {
		// Replace password portion with masked value
		return uri.replaceAll("mongodb\\+srv://[^:]+:([^@]+)@", "mongodb+srv://[username]:[PASSWORD]@");
	}
}
