package ru.kradin.murder_at_the_hotel.game;

import ru.kradin.murder_at_the_hotel.enums.GameSessionNotifyType;

public interface GameSessionObserver {
    void update(GameSession gameSession, GameSessionNotifyType gameSessionNotifyType);
    boolean isGameSessionIdInUse(String gameSessionId);
}
