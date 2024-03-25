package ru.kradin.murder_at_the_hotel.game;

public interface GameSessionObserver {
    void update(GameSession gameSession);
    boolean isGameSessionIdInUse(String gameSessionId);
}
