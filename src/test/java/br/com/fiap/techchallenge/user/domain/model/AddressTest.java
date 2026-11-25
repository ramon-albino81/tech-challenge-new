package br.com.fiap.techchallenge.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiap.techchallenge.user.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AddressTest {

    @Test
    void shouldCreateValidAddressAndTrimFields() {
        Address address = new Address("  Rua Paulista  ", " 1000A ", " Sao Paulo ", " 01310-100 ");

        assertThat(address.street()).isEqualTo("Rua Paulista");
        assertThat(address.number()).isEqualTo("1000A");
        assertThat(address.city()).isEqualTo("Sao Paulo");
        assertThat(address.postalCode()).isEqualTo("01310-100");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingStreet(String street) {
        assertThatThrownBy(() -> new Address(street, "123", "Sao Paulo", "01000-000"))
                .isInstanceOf(DomainValidationException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingNumber(String number) {
        assertThatThrownBy(() -> new Address("Rua Exemplo", number, "Sao Paulo", "01000-000"))
                .isInstanceOf(DomainValidationException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectMissingCity(String city) {
        assertThatThrownBy(() -> new Address("Rua Exemplo", "123", city, "01000-000"))
                .isInstanceOf(DomainValidationException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "0", "01000@000", "!!!"})
    void shouldRejectInvalidPostalCode(String postalCode) {
        assertThatThrownBy(() -> new Address("Rua Exemplo", "123", "Sao Paulo", postalCode))
                .isInstanceOf(DomainValidationException.class);
    }
}
