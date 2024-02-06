package ru.kradin.game.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kradin.game.models.ChatState;

public interface ChatStateRepository extends JpaRepository<ChatState, Long> {
}
