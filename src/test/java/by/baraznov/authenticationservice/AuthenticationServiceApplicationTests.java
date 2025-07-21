package by.baraznov.authenticationservice;

import by.baraznov.authenticationservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Import(TestContainersConfig.class)
@Testcontainers
@ActiveProfiles("test")
class AuthenticationServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
