package br.com.fiap.techchallenge.user.domain.model;

import br.com.fiap.techchallenge.user.domain.exception.DomainValidationException;
import java.util.Locale;

public enum UserType {
    DONO_RESTAURANTE,
    CLIENTE;

    public static UserType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("Tipo de usuario e obrigatorio");
        }
        try {
            return UserType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("Tipo de usuario invalido: " + value.trim());
        }
    }
}
