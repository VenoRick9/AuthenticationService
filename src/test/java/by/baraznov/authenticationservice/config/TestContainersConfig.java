package by.baraznov.authenticationservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration
public class TestContainersConfig {
    private static final String IMAGE_NAME = "postgres:13";
    private static final String DB_NAME = "auth-service";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "Postgres_9";


    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(IMAGE_NAME)
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USERNAME)
            .withPassword(DB_PASSWORD);

    static {
        postgres.start();
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }
}
