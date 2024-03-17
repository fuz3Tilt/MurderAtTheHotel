package ru.kradin.murder_at_the_hotel.game;

import ru.kradin.murder_at_the_hotel.enums.GameSessionNotifyType;
import ru.kradin.murder_at_the_hotel.models.Player;
import ru.kradin.murder_at_the_hotel.room.Room;
import ru.kradin.murder_at_the_hotel.services.ItemAssignerService;
import ru.kradin.murder_at_the_hotel.services.RoleAssignerService;
import ru.kradin.murder_at_the_hotel.utils.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameSession {
    private static final int GAME_STARTED_STAGE = 0;
    private static final int FIRST_DISCUSSION_STAGE = 1;
    private static final int FIRST_VOTING_STAGE = 2;
    private static final int NIGHT_STAGE = 3;
    private static final int DISCUSSION_STAGE = 4;
    private static final int VOTING_STAGE = 5;
    private static final int GAME_ENDED_STAGE = 6;
    private String id;
    private GameSessionObserver gameSessionObserver;
    private int stage;
    private List<Gamer> gamers;
    private Room room;
    private RoleAssignerService roleAssignerService;
    private ItemAssignerService itemAssignerService;
    private Queue<String> messagesToPlayers;


    public GameSession(Room room, GameSessionObserver gameSessionObserver, RoleAssignerService roleAssignerService, ItemAssignerService itemAssignerService) {
        this.room = room;
        this.gameSessionObserver = gameSessionObserver;
        this.roleAssignerService = roleAssignerService;
        this.itemAssignerService = itemAssignerService;

        stage = GAME_STARTED_STAGE;

        setId();

        messagesToPlayers = new LinkedList<>();

        gamers = new ArrayList<>();
        for (Player player: room.getPlayers()) {
            gamers.add(new Gamer(player.getChatId(), player.getNickname()));
        }

        roleAssignerService.assignRoles(gamers);
        itemAssignerService.assignItems(gamers);

        gameSessionObserver.update(this, GameSessionNotifyType.GAME_STARTED);

        startGame();
    }

    public String getId() {
        return id;
    }

    private void setId() {
        String preId = IdGenerator.generate();
        while(gameSessionObserver.isGameSessionIdInUse(preId)) {
            preId = IdGenerator.generate();
        }
        id = preId;
    }

    private void startGame() {
        //код переключения состояний
    }

    public void addMessageToPlayers(String message) {
        messagesToPlayers.add(message);
    }
}
