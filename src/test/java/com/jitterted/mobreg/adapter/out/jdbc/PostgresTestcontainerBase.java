package com.jitterted.mobreg.adapter.out.jdbc;

import org.junit.jupiter.api.Tag;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Tag("container")
/* NOTE: For reusing a container (faster test runs), this setup requires the file
 * testcontainers.properties in your "home" directory (e.g., ~/) to have this property set:
 *
 *      testcontainers.reuse.enable=true
 *
 * You will need to manually clean up running containers.
 */
@Testcontainers(disabledWithoutDocker = true)
@Transactional
public abstract class PostgresTestcontainerBase {

    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

    static {
        //noinspection resource
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"))
                .withDatabaseName("postgrestest")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);

        POSTGRESQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void datasourceConfiguration(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.sql.init.platform", () -> "postgresql");
    }
}
