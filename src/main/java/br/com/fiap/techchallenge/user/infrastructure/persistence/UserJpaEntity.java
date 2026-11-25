package br.com.fiap.techchallenge.user.infrastructure.persistence;

import br.com.fiap.techchallenge.user.domain.model.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_app_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uq_app_user_login", columnNames = "login")
        }
)
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "login", nullable = false, length = 100)
    private String login;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 32)
    private UserType userType;

    @Column(name = "last_modified_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant lastModifiedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address", nullable = false, columnDefinition = "json")
    private AddressJson address;

    protected UserJpaEntity() {
    }

    public UserJpaEntity(
            Long id,
            String name,
            String email,
            String login,
            String passwordHash,
            UserType userType,
            Instant lastModifiedAt,
            AddressJson address
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.passwordHash = passwordHash;
        this.userType = userType;
        this.lastModifiedAt = lastModifiedAt;
        this.address = address;
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

    public AddressJson getAddress() {
        return address;
    }
}
