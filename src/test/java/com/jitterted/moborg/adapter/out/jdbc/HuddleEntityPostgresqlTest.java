package com.jitterted.moborg.adapter.out.jdbc;

import com.jitterted.moborg.domain.Huddle;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Tag("container")
@Tag("integration")
class HuddleEntityPostgresqlTest {

    @Autowired
    JdbcHuddleRepository jdbcHuddleRepository;

    // create shared container with a container image name "postgres" and latest major release of PostgreSQL "13"
    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("posttest")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.sql.init.platform", () -> "postgresql");
    }

    @Test
    public void huddleEntityStoredViaJdbcIsRetrievedAsOriginal() throws Exception {
        Huddle original = new Huddle("entity", ZonedDateTime.now());
        HuddleEntity originalEntity = HuddleEntity.from(original);

        HuddleEntity savedEntity = jdbcHuddleRepository.save(originalEntity);

        Optional<HuddleEntity> retrievedEntity = jdbcHuddleRepository.findById(savedEntity.getId());

        assertThat(retrievedEntity)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(originalEntity);
    }

}