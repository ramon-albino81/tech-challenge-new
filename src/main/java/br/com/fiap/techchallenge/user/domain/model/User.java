package br.com.fiap.techchallenge.user.domain.model;

import br.com.fiap.techchallenge.user.domain.exception.DomainValidationException;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class User {

    public static final int MAX_NAME_LENGTH = 150;
    public static final int MAX_EMAIL_LENGTH = 254;
    public static final int MAX_LOGIN_LENGTH = 100;
    public static final int MAX_PASSWORD_HASH_LENGTH = 255;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final Long id;
    private final String name;
    private final String email;
    private final String login;
    private final String passwordHash;
    private final UserType userType;
    private final Instant lastModifiedAt;
    private final Address address;

    private User(
            Long id,
            String name,
            String email,
            String login,
            String passwordHash,
            UserType userType,
            Instant lastModifiedAt,
            Address address
    ) {
        this.id = id;
        this.name = validateName(name);
        this.email = validateAndNormalizeEmail(email);
        this.login = validateAndNormalizeLogin(login);
        this.passwordHash = validatePasswordHash(passwordHash);
        this.userType = requireUserType(userType);
        this.lastModifiedAt = requireLastModifiedAt(lastModifiedAt);
        this.address = requireAddress(address);
    }

    public static User createNew(
            String name,
            String email,
            String login,
            String passwordHash,
            UserType userType,
            Address address,
            Instant createdAt
    ) {
        return new User(null, name, email, login, passwordHash, userType, createdAt, address);
    }

    public static User reconstitute(
            Long id,
            String name,
            String email,
            String login,
            String passwordHash,
            UserType userType,
            Instant lastModifiedAt,
            Address address
    ) {
        if (id == null || id <= 0) {
            throw new DomainValidationException("Identificador do usuario e invalido");
        }
        return new User(id, name, email, login, passwordHash, userType, lastModifiedAt, address);
    }

    public User updateProfile(
            String newName,
            String newEmail,
            String newLogin,
            Address newAddress,
            Instant modifiedAt
    ) {
        return new User(
                this.id,
                newName,
                newEmail,
                newLogin,
                this.passwordHash,
                this.userType,
                modifiedAt,
                newAddress
        );
    }

    public User changePassword(String newPasswordHash, Instant modifiedAt) {
        return new User(
                this.id,
                this.name,
                this.email,
                this.login,
                newPasswordHash,
                this.userType,
                modifiedAt,
                this.address
        );
    }

    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static String normalizeLogin(String login) {
        if (login == null) {
            return null;
        }
        return login.trim().toLowerCase(Locale.ROOT);
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Nome e obrigatorio");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new DomainValidationException("Nome excede o limite de " + MAX_NAME_LENGTH + " caracteres");
        }
        return trimmed;
    }

    private static String validateAndNormalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainValidationException("E-mail e obrigatorio");
        }
        String normalized = normalizeEmail(email);
        if (normalized.length() > MAX_EMAIL_LENGTH) {
            throw new DomainValidationException("E-mail excede o limite de " + MAX_EMAIL_LENGTH + " caracteres");
        }
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new DomainValidationException("E-mail possui formato invalido");
        }
        return normalized;
    }

    private static String validateAndNormalizeLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new DomainValidationException("Login e obrigatorio");
        }
        String normalized = normalizeLogin(login);
        if (normalized.length() > MAX_LOGIN_LENGTH) {
            throw new DomainValidationException("Login excede o limite de " + MAX_LOGIN_LENGTH + " caracteres");
        }
        if (!LOGIN_PATTERN.matcher(normalized).matches()) {
            throw new DomainValidationException("Login possui formato invalido");
        }
        return normalized;
    }

    private static String validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainValidationException("Senha e obrigatoria");
        }
        if (passwordHash.length() > MAX_PASSWORD_HASH_LENGTH) {
            throw new DomainValidationException("Hash de senha excede o limite de " + MAX_PASSWORD_HASH_LENGTH + " caracteres");
        }
        return passwordHash;
    }

    private static UserType requireUserType(UserType userType) {
        if (userType == null) {
            throw new DomainValidationException("Tipo de usuario e obrigatorio");
        }
        return userType;
    }

    private static Instant requireLastModifiedAt(Instant lastModifiedAt) {
        if (lastModifiedAt == null) {
            throw new DomainValidationException("Data da ultima alteracao e obrigatoria");
        }
        return lastModifiedAt;
    }

    private static Address requireAddress(Address address) {
        if (address == null) {
            throw new DomainValidationException("Endereco e obrigatorio");
        }
        return address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserType getUserType() {
        return userType;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User user)) {
            return false;
        }
        return Objects.equals(id, user.id)
                && Objects.equals(name, user.name)
                && Objects.equals(email, user.email)
                && Objects.equals(login, user.login)
                && Objects.equals(passwordHash, user.passwordHash)
                && userType == user.userType
                && Objects.equals(lastModifiedAt, user.lastModifiedAt)
                && Objects.equals(address, user.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, login, passwordHash, userType, lastModifiedAt, address);
    }

    @Override
    public String toString() {
        return "User[id=" + id
                + ", name=" + name
                + ", email=" + email
                + ", login=" + login
                + ", userType=" + userType
                + ", lastModifiedAt=" + lastModifiedAt
                + ", address=" + address + "]";
    }
}
