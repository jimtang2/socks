package studio.lab9.socks.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.cdimascio.dotenv.Dotenv;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class DatabaseConfig {
	static {
		Dotenv dotenv = Dotenv.configure()
		        .ignoreIfMissing()
		        .ignoreIfMalformed()
		        .systemProperties()
		        .load();
	}

    private String jdbcUrl() {
    	return "jdbc:postgresql://" + System.getProperty("PGHOST") + ":" + System.getProperty("PGPORT") + "/" + System.getProperty("PGDATABASE");
    }

    private String username() {
    	return System.getProperty("PGUSER");
    }

    private String password() {
    	return System.getProperty("PGPASSWORD");
    }

    @Bean
    public DataSource dataSource() {
        readProperties();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl());
        config.setUsername(username());
        config.setPassword(password());
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setInitializationFailTimeout(0);
        config.addDataSourceProperty("reWriteBatchedInserts", "true");

        return new HikariDataSource(config);
    }

    private void readProperties() throws IllegalStateException {
        String[] params = {"PGHOST", "PGPORT", "PGDATABASE", "PGUSER", "PGPASSWORD"};
        String missingKeys = Arrays.stream(params)
                .filter(key -> {
                    String value = System.getenv(key) != null ? System.getenv(key) : System.getProperty(key);
                    if (value != null) {
                        System.setProperty(key, value);
                    }
                    return value == null;
                })
                .collect(Collectors.joining(", "));

        if (!missingKeys.isEmpty()) {
            throw new IllegalStateException("Missing required database configuration properties: " + missingKeys);
        }
    }
}