package ru.kradin.murder_at_the_hotel.game;

import ru.kradin.murder_at_the_hotel.room.Room;
import ru.kradin.murder_at_the_hotel.services.ItemAssignerService;
import ru.kradin.murder_at_the_hotel.services.RoleAssignerService;

public class GameSessionFactory {
    private final GameSessionObserver gameSessionObserver;
    private final RoleAssignerService roleAssignerService;
    private final ItemAssignerService itemAssignerService;

    public GameSessionFactory(GameSessionObserver gameSessionObserver, RoleAssignerService roleAssignerService, ItemAssignerService itemAssignerService) {
        this.gameSessionObserver = gameSessionObserver;
        this.roleAssignerService = roleAssignerService;
        this.itemAssignerService = itemAssignerService;
    }

    public GameSession createGameSession(Room room) {
        return new GameSession(room, gameSessionObserver, roleAssignerService, itemAssignerService);
    }
}
