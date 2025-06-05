package studio.lab9.socks.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class TestConfig {
	public static class DotenvTestExecutionListener extends AbstractTestExecutionListener {
		@Override
		public void beforeTestClass(TestContext testContext) {
			String workingDir = Paths.get(".").toAbsolutePath().normalize().toString();
			Path envPath = Paths.get(workingDir, ".env");
			System.err.println("Working directory: " + workingDir);
			System.err.println("Attempting to load .env from: " + envPath);
			System.err.println(".env exists: " + Files.exists(envPath));
			try {
				if (Files.exists(envPath)) {
					System.err.println(".env content: " + Files.readString(envPath));
				}
				Dotenv dotenv = Dotenv.configure()
						.directory(workingDir)
						.filename(".env")
						.ignoreIfMissing()
						.systemProperties()
						.load();
				System.err.println("PGHOST: " + System.getenv("PGHOST"));
				System.err.println("PGPORT: " + System.getenv("PGPORT"));
				System.err.println("PGDATABASE: " + System.getenv("PGDATABASE"));
				System.err.println("PGUSER: " + System.getenv("PGUSER"));
				System.err.println("PGPASSWORD: " + (System.getenv("PGPASSWORD") != null ? "****" : "null"));
			} catch (Exception e) {
				System.err.println("Error loading .env: " + e.getMessage());
			}
		}
	}
}