package br.com.fiap.techchallenge.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.techchallenge.user.domain.exception.DomainValidationException;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UserTest {

    private static final Instant NOW = Instant.parse("2026-11-24T20:00:00Z");
    private static final Address VALID_ADDRESS = new Address("Rua Exemplo", "123", "Sao Paulo", "01000-000");

    @Test
    void shouldCreateValidUserWithNormalizedEmailAndLogin() {
        User user = User.createNew(
                "  Maria Silva  ",
                "  Maria.Silva@Example.COM ",
                "  Maria.Silva ",
                "$2a$10$hashedPasswordValue",
                UserType.DONO_RESTAURANTE,
                VALID_ADDRESS,
                NOW
        );

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo("Maria Silva");
        assertThat(user.getEmail()).isEqualTo("maria.silva@example.com");
        assertThat(user.getLogin()).isEqualTo("maria.silva");
        assertThat(user.getPasswordHash()).isEqualTo("$2a$10$hashedPasswordValue");
        assertThat(user.getUserType()).isEqualTo(UserType.DONO_RESTAURANTE);
        assertThat(user.getLastModifiedAt()).isEqualTo(NOW);
        assertThat(user.getAddress()).isEqualTo(VALID_ADDRESS);
        assertThat(user.toString()).doesNotContain("$2a$10$hashedPasswordValue");
    }

    @Test
    void shouldUpdateProfilePreservingPasswordAndType() {
        User original = User.reconstitute(
                10L,
                "Maria Silva",
                "maria@example.com",
                "maria",
                "$2a$10$originalHash",
                UserType.CLIENTE,
                NOW,
                VALID_ADDRESS
        );
        Instant later = NOW.plusSeconds(60);
        Address updatedAddress = new Address("Av Brasil", "500", "Campinas", "13010-000");

        User updated = original.updateProfile("Maria Souza", "maria.souza@example.com", "mariasouza", updatedAddress, later);

        assertThat(updated.getId()).isEqualTo(10L);
        assertThat(updated.getName()).isEqualTo("Maria Souza");
        assertThat(updated.getEmail()).isEqualTo("maria.souza@example.com");
        assertThat(updated.getLogin()).isEqualTo("mariasouza");
        assertThat(updated.getPasswordHash()).isEqualTo("$2a$10$originalHash");
        assertThat(updated.getUserType()).isEqualTo(UserType.CLIENTE);
        assertThat(updated.getLastModifiedAt()).isEqualTo(later);
        assertThat(updated.getAddress()).isEqualTo(updatedAddress);
    }

    @Test
    void shouldChangePasswordPreservingProfileAndUpdatingTimestamp() {
        User original = User.reconstitute(
                10L,
                "Maria Silva",
                "maria@example.com",
                "maria",
                "$2a$10$originalHash",
                UserType.CLIENTE,
                NOW,
                VALID_ADDRESS
        );
        Instant later = NOW.plusSeconds(120);

        User updated = original.changePassword("$2a$10$newHashValue", later);

        assertThat(updated.getPasswordHash()).isEqualTo("$2a$10$newHashValue");
        assertThat(updated.getLastModifiedAt()).isEqualTo(later);
        assertThat(updated.getEmail()).isEqualTo("maria@example.com");
        assertThat(updated.getUserType()).isEqualTo(UserType.CLIENTE);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingName(String name) {
        assertThatThrownBy(() -> User.createNew(name, "a@b.com", "login", "$2a$hash", UserType.CLIENTE, VALID_ADDRESS, NOW))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldRejectNameLongerThanLimit() {
        String tooLong = "a".repeat(User.MAX_NAME_LENGTH + 1);
        assertThatThrownBy(() -> User.createNew(tooLong, "a@b.com", "login", "$2a$hash", UserType.CLIENTE, VALID_ADDRESS, NOW))
                .isInstanceOf(DomainValidationException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "invalido", "sem-dominio@", "@sem-usuario.com", "a@b"})
    void shouldRejectInvalidEmail(String email) {
        assertThatThrownBy(() -> User.createNew("Nome", email, "login", "$2a$hash", UserType.CLIENTE, VALID_ADDRESS, NOW))
                .isInstanceOf(DomainValidationException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "com espaco", "login@invalido"})
    void shouldRejectInvalidLogin(String login) {
        assertThatThrownBy(() -> User.createNew("Nome", "a@b.com", login, "$2a$hash", UserType.CLIENTE, VALID_ADDRESS, NOW))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldRejectMissingPasswordHashTypeAddressOrTimestamp() {
        assertThatThrownBy(() -> User.createNew("Nome", "a@b.com", "login", " ", UserType.CLIENTE, VALID_ADDRESS, NOW))
                .isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> User.createNew("Nome", "a@b.com", "login", "$2a$hash", null, VALID_ADDRESS, NOW))
                .isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> User.createNew("Nome", "a@b.com", "login", "$2a$hash", UserType.CLIENTE, null, NOW))
                .isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> User.createNew("Nome", "a@b.com", "login", "$2a$hash", UserType.CLIENTE, VALID_ADDRESS, null))
                .isInstanceOf(DomainValidationException.class);
    }
}
