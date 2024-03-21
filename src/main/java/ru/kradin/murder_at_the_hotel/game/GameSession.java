package ru.kradin.murder_at_the_hotel.game;

import ru.kradin.murder_at_the_hotel.enums.GameSessionNotifyType;
import ru.kradin.murder_at_the_hotel.enums.GameStage;
import ru.kradin.murder_at_the_hotel.models.Player;
import ru.kradin.murder_at_the_hotel.room.Room;
import ru.kradin.murder_at_the_hotel.room.RoomSettings;
import ru.kradin.murder_at_the_hotel.services.ItemAssignerService;
import ru.kradin.murder_at_the_hotel.services.RoleAssignerService;
import ru.kradin.murder_at_the_hotel.utils.IdGenerator;

import java.util.*;

public class GameSession {
    private String id;
    private GameSessionObserver gameSessionObserver;
    private GameStage stage;
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

        stage = GameStage.INTRODUCTION;

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

    public Room getRoom() {
        return room;
    }

    public List<Gamer> getGamers() {
        return gamers;
    }

    public GameStage getStage() {
        return stage;
    }

    private void startGame() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                switch (stage) {
                    case INTRODUCTION:
                        stage = GameStage.FIRST_DISCUSSION;

                        gameSessionObserver.update(GameSession.this,GameSessionNotifyType.STAGE_CHANGED);

                        break;
                    case FIRST_DISCUSSION:
                        stage = GameStage.FIRST_VOTING;

                        gameSessionObserver.update(GameSession.this,GameSessionNotifyType.STAGE_CHANGED);

                        break;
                    case FIRST_VOTING:
                        stage = GameStage.NIGHT;

                        gameSessionObserver.update(GameSession.this,GameSessionNotifyType.STAGE_CHANGED);

                        break;
                    case NIGHT:
                        stage = GameStage.DISCUSSION;

                        gameSessionObserver.update(GameSession.this,GameSessionNotifyType.STAGE_CHANGED);

                        break;
                    case DISCUSSION:
                        stage = GameStage.VOTING;

                        gameSessionObserver.update(GameSession.this,GameSessionNotifyType.STAGE_CHANGED);

                        break;
                    case VOTING:
                        stage = GameStage.NIGHT;

                        gameSessionObserver.update(GameSession.this,GameSessionNotifyType.STAGE_CHANGED);

                        break;
                }
            }
        };
        int taskRepeatTime = 0;
        if (room.getRoomSettings().getSpeedType().equals(RoomSettings.SpeedType.NORMAL)) {
            taskRepeatTime = 1000*60;
        } else {
            taskRepeatTime = 1000*30;
        }
        timer.schedule(timerTask, 1000*30, taskRepeatTime);
    }

    public void addMessageToPlayers(String message) {
        messagesToPlayers.add(message);
    }
}
