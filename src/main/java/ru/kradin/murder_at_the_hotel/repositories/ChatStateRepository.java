package ru.kradin.murder_at_the_hotel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kradin.murder_at_the_hotel.models.ChatState;

public interface ChatStateRepository extends JpaRepository<ChatState, Long> {
}
