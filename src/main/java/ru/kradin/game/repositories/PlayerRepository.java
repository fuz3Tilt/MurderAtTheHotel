package ru.kradin.game.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kradin.game.models.Player;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByNickname(String nickname);
}
