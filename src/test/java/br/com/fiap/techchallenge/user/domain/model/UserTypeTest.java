package br.com.fiap.techchallenge.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.techchallenge.user.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UserTypeTest {

    @Test
    void shouldAcceptSupportedUserTypes() {
        assertThat(UserType.fromString("DONO_RESTAURANTE")).isEqualTo(UserType.DONO_RESTAURANTE);
        assertThat(UserType.fromString("CLIENTE")).isEqualTo(UserType.CLIENTE);
        assertThat(UserType.fromString("  cliente  ")).isEqualTo(UserType.CLIENTE);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "ADMIN", "GERENTE"})
    void shouldRejectUnsupportedOrMissingUserType(String value) {
        assertThatThrownBy(() -> UserType.fromString(value))
                .isInstanceOf(DomainValidationException.class);
    }
}
