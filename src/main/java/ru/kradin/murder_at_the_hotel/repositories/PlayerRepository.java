package ru.kradin.murder_at_the_hotel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kradin.murder_at_the_hotel.models.Player;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT p FROM Player p WHERE LOWER(p.nickname) = LOWER(:nickname)")
    Optional<Player> findByNicknameIgnoreCase(@Param("nickname") String nickname);
}
