package br.com.fiap.techchallenge.user.domain.model;

import br.com.fiap.techchallenge.user.domain.exception.DomainValidationException;
import java.util.regex.Pattern;

public record Address(String street, String number, String city, String postalCode) {

    private static final int MAX_STREET_LENGTH = 200;
    private static final int MAX_NUMBER_LENGTH = 30;
    private static final int MAX_CITY_LENGTH = 120;
    private static final int MAX_POSTAL_CODE_LENGTH = 20;
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9A-Za-z][0-9A-Za-z\\s-]{1,18}[0-9A-Za-z]$");

    public Address {
        street = requireNonBlank(street, "Rua", MAX_STREET_LENGTH);
        number = requireNonBlank(number, "Numero", MAX_NUMBER_LENGTH);
        city = requireNonBlank(city, "Cidade", MAX_CITY_LENGTH);
        postalCode = requireValidPostalCode(postalCode);
    }

    private static String requireNonBlank(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(fieldName + " e obrigatorio");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new DomainValidationException(fieldName + " excede o limite de " + maxLength + " caracteres");
        }
        return trimmed;
    }

    private static String requireValidPostalCode(String value) {
        String trimmed = requireNonBlank(value, "CEP", MAX_POSTAL_CODE_LENGTH);
        if (!POSTAL_CODE_PATTERN.matcher(trimmed).matches()) {
            throw new DomainValidationException("CEP possui formato invalido");
        }
        return trimmed;
    }
}
