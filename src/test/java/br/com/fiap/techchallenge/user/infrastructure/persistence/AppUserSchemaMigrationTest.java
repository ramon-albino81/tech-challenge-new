package br.com.fiap.techchallenge.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.fiap.techchallenge.user.domain.model.UserType;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppUserSchemaMigrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4.3");

    @Autowired
    private SpringDataUserRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldApplyFlywayMigrationAndMatchJpaMapping() {
        @SuppressWarnings("unchecked")
        List<String> columns = entityManager.createNativeQuery(
                "SELECT column_name FROM information_schema.columns "
                        + "WHERE table_schema = DATABASE() AND table_name = 'app_user' "
                        + "ORDER BY ordinal_position"
        ).getResultList();

        assertThat(columns).containsExactly(
                "id",
                "name",
                "email",
                "login",
                "password_hash",
                "user_type",
                "last_modified_at",
                "address"
        );

        Object collation = entityManager.createNativeQuery(
                "SELECT table_collation FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() AND table_name = 'app_user'"
        ).getSingleResult();
        assertThat(collation).isEqualTo("utf8mb4_0900_ai_ci");

        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        UserJpaEntity entity = new UserJpaEntity(
                null,
                "Dono Teste",
                "dono.teste@example.com",
                "dono.teste",
                "$2a$10$abcdefghijklmnopqrstuv123456789012345678901234567890",
                UserType.DONO_RESTAURANTE,
                now,
                new AddressJson("Rua Exemplo", "123", "Sao Paulo", "01000-000")
        );

        UserJpaEntity saved = repository.saveAndFlush(entity);
        entityManager.clear();

        UserJpaEntity reloaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("Dono Teste");
        assertThat(reloaded.getEmail()).isEqualTo("dono.teste@example.com");
        assertThat(reloaded.getLogin()).isEqualTo("dono.teste");
        assertThat(reloaded.getUserType()).isEqualTo(UserType.DONO_RESTAURANTE);
        assertThat(reloaded.getLastModifiedAt()).isEqualTo(now);
        assertThat(reloaded.getAddress()).isEqualTo(new AddressJson("Rua Exemplo", "123", "Sao Paulo", "01000-000"));
    }
}
