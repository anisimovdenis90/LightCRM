package ru.lightcrm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lightcrm.entities.User;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(@NotNull String login);

    boolean existsByLogin(@NotNull String login);
}
