package ru.kradin.murder_at_the_hotel.game;

import ru.kradin.murder_at_the_hotel.models.Player;
import ru.kradin.murder_at_the_hotel.room.Room;
import ru.kradin.murder_at_the_hotel.services.ItemAssignerService;
import ru.kradin.murder_at_the_hotel.services.RoleAssignerService;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private static final int GAME_STARTED_STAGE = 0;
    private static final int FIRST_DISCUSSION_STAGE = 1;
    private static final int FIRST_VOTING_STAGE = 2;
    private static final int NIGHT_STAGE = 3;
    private static final int DISCUSSION_STAGE = 4;
    private static final int VOTING_STAGE = 5;
    private static final int GAME_ENDED_STAGE = 6;
    private int stage;
    private List<Gamer> gamers;
    private Room room;
    private RoleAssignerService roleAssignerService;
    private ItemAssignerService itemAssignerService;


    public GameSession(Room room) {
        this.room = room;
        stage = GAME_STARTED_STAGE;

        gamers = new ArrayList<>();
        for (Player player: room.getPlayers()) {
            gamers.add(new Gamer(player.getChatId(), player.getNickname()));
        }

        roleAssignerService.assignRoles(gamers);
        itemAssignerService.assignItems(gamers);


        startGame();
    }

    private void startGame() {

    }
}
