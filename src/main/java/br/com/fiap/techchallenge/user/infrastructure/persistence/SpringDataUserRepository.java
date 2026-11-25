package br.com.fiap.techchallenge.user.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByLogin(String login);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    Page<UserJpaEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
