package ru.kradin.murder_at_the_hotel.services;

import org.springframework.stereotype.Service;
import ru.kradin.murder_at_the_hotel.game.GameSession;
import ru.kradin.murder_at_the_hotel.game.GameSessionFactory;
import ru.kradin.murder_at_the_hotel.game.GameSessionObserver;
import ru.kradin.murder_at_the_hotel.room.Room;

@Service
public class GameSessionService {
    private GameSessionFactory gameSessionFactory;

    public GameSessionService(GameSessionObserver gameSessionObserver, RoleAssignerService roleAssignerService, ItemAssignerService itemAssignerService) {
        gameSessionFactory = new GameSessionFactory(gameSessionObserver, roleAssignerService, itemAssignerService);
    }

    public GameSession startGame(Room room) {
        room.setNotSearchable();
        return gameSessionFactory.createGameSession(room);
    }
}
