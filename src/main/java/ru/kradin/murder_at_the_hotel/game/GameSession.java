package ru.kradin.murder_at_the_hotel.game;

import ru.kradin.murder_at_the_hotel.enums.GameSessionNotifyType;
import ru.kradin.murder_at_the_hotel.enums.GameStage;
import ru.kradin.murder_at_the_hotel.game.items.Item;
import ru.kradin.murder_at_the_hotel.models.Player;
import ru.kradin.murder_at_the_hotel.room.Room;
import ru.kradin.murder_at_the_hotel.room.RoomSettings;
import ru.kradin.murder_at_the_hotel.services.ItemAssignerService;
import ru.kradin.murder_at_the_hotel.services.RoleAssignerService;
import ru.kradin.murder_at_the_hotel.utils.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class GameSession {
    private String id;
    private GameSessionObserver gameSessionObserver;
    private GameStage stage;
    private List<Gamer> gamers;
    private Room room;
    private RoleAssignerService roleAssignerService;
    private ItemAssignerService itemAssignerService;
    private Queue<String> messagesToPlayers;
    private Map<Gamer, Integer> votesCountMap;
    private Gamer nextTourGamer;
    private boolean nextTour;
    private List<Gamer> nextTourParticipants;


    public GameSession(Room room, GameSessionObserver gameSessionObserver, RoleAssignerService roleAssignerService, ItemAssignerService itemAssignerService) {
        this.room = room;
        this.gameSessionObserver = gameSessionObserver;

        stage = GameStage.INTRODUCTION;

        setId();

        messagesToPlayers = new LinkedList<>();
        votesCountMap = new HashMap<>();
        nextTourParticipants = new ArrayList<>();
        nextTour = false;

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
                    case FIRST_VOTING: {
                        nextTourGamer = null;
                        nextTour = false;
                        nextTourParticipants.clear();

                        List<Gamer> gamersWithMaxVotes = new ArrayList<>();
                        List<Gamer> gamersWithSecondMaxVotes = new ArrayList<>();

                        StringBuilder textBuilder = new StringBuilder();

                        if (!votesCountMap.keySet().isEmpty()) {
                            textBuilder.append("Распределение голосов:\n");

                            int maxVotes = 0;
                            int secondMaxVotes = 0;

                            int totalPlayers = votesCountMap.keySet().size();
                            int count = 0;
                            for (Gamer gamer : votesCountMap.keySet()) {
                                int currentVotes = votesCountMap.get(gamer);

                                textBuilder.append(gamer.getNickname());
                                textBuilder.append(" - ");
                                textBuilder.append(currentVotes);
                                if (++count < totalPlayers) {
                                    textBuilder.append("\n");
                                }

                                if (currentVotes > maxVotes) {
                                    secondMaxVotes = maxVotes;
                                    gamersWithSecondMaxVotes.clear();
                                    gamersWithSecondMaxVotes.addAll(gamersWithMaxVotes);

                                    maxVotes = currentVotes;
                                    gamersWithMaxVotes.clear();
                                    gamersWithMaxVotes.add(gamer);
                                } else if (currentVotes == maxVotes) {
                                    gamersWithMaxVotes.add(gamer);
                                } else if (currentVotes > secondMaxVotes) {
                                    secondMaxVotes = currentVotes;
                                    gamersWithSecondMaxVotes.clear();
                                    gamersWithSecondMaxVotes.add(gamer);
                                } else if (currentVotes == secondMaxVotes) {
                                    gamersWithSecondMaxVotes.add(gamer);
                                }
                            }
                        }
                        StringBuilder infoBuilder = new StringBuilder();
                        if (gamersWithMaxVotes.size() == 0) {
                            infoBuilder.append("По результатам голосования не будет предпринято никаких действий.");
                        } else if (gamersWithMaxVotes.size() == 1) {
                            if (gamersWithSecondMaxVotes.size() == 0) {
                                Gamer gamer = gamersWithMaxVotes.get(0);
                                int bagSize = gamer.getBag().getItems().size();
                                if (bagSize == 0) {
                                    infoBuilder.append("У ").append(gamer.getNickname()).append(" нет предметов.");
                                } else {
                                    Random random = new Random();
                                    Item itemToShow = gamer.getBag().getItems().get(random.nextInt(bagSize));
                                    infoBuilder.append("У ").append(gamer.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".");
                                }
                            } else if (gamersWithSecondMaxVotes.size() == 1) {
                                Gamer gamer1 = gamersWithMaxVotes.get(0);
                                int bagSize = gamer1.getBag().getItems().size();
                                if (bagSize == 0) {
                                    infoBuilder.append("У ").append(gamer1.getNickname()).append(" нет предметов.\n");
                                } else {
                                    Random random = new Random();
                                    Item itemToShow = gamer1.getBag().getItems().get(random.nextInt(bagSize));
                                    infoBuilder.append("У ").append(gamer1.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".\n");
                                }
                                Gamer gamer2 = gamersWithSecondMaxVotes.get(0);
                                infoBuilder.append("У ").append(gamer2.getNickname()).append(" \"").append(gamer2.getRole().getRoleColor().getColor()).append("\" цвет.");
                            } else if (gamersWithSecondMaxVotes.size() > 2) {
                                nextTourGamer = gamersWithMaxVotes.get(0);
                                nextTour = true;
                                nextTourParticipants = gamersWithSecondMaxVotes;
                            }
                        } else if (gamersWithMaxVotes.size() == 2) {
                            Gamer gamer1 = gamersWithMaxVotes.get(0);
                            int bagSize = gamer1.getBag().getItems().size();
                            if (bagSize == 0) {
                                infoBuilder.append("У ").append(gamer1.getNickname()).append(" нет предметов.\n");
                            } else {
                                Random random = new Random();
                                Item itemToShow = gamer1.getBag().getItems().get(random.nextInt(bagSize));
                                infoBuilder.append("У ").append(gamer1.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".\n");
                            }
                            Gamer gamer2 = gamersWithMaxVotes.get(1);
                            infoBuilder.append("У ").append(gamer2.getNickname()).append(" \"").append(gamer2.getRole().getRoleColor().getColor()).append("\" цвет.");
                        } else if (gamersWithMaxVotes.size() > 2) {
                            nextTour = true;
                            nextTourParticipants = gamersWithMaxVotes;
                        }

                        if (nextTour) {
                            stage = GameStage.EXTRA_FIRST_VOTING;
                        } else {
                            stage = GameStage.NIGHT;
                        }

                        messagesToPlayers.add(textBuilder.toString());
                        messagesToPlayers.add(infoBuilder.toString());
                        votesCountMap.clear();
                        gameSessionObserver.update(GameSession.this, GameSessionNotifyType.STAGE_CHANGED);
                        break;
                    }
                    case EXTRA_FIRST_VOTING:
                        messagesToPlayers.clear();
                        StringBuilder infoBuilder = new StringBuilder();
                        if (nextTourGamer != null) {
                            StringBuilder textBuilder = new StringBuilder();
                            textBuilder.append("Распределение дополнительных голосов:\n");

                            List<Gamer> gamersWithMaxVotes = new ArrayList<>();
                            int maxVotes = 0;

                            int totalPlayers = votesCountMap.keySet().size();
                            int count = 0;
                            for (Gamer gamer : votesCountMap.keySet()) {
                                int currentVotes = votesCountMap.get(gamer);

                                textBuilder.append(gamer.getNickname());
                                textBuilder.append(" - ");
                                textBuilder.append(currentVotes);
                                if (++count < totalPlayers) {
                                    textBuilder.append("\n");
                                }

                                if (currentVotes > maxVotes) {
                                    maxVotes = currentVotes;
                                    gamersWithMaxVotes.clear();
                                    gamersWithMaxVotes.add(gamer);
                                } else if (currentVotes == maxVotes) {
                                    gamersWithMaxVotes.add(gamer);
                                }
                            }

                            if (gamersWithMaxVotes.size() == 0) {
                                infoBuilder.append("По результатам голосования будет показана информация об игроке, набравшем максимальное количество голосов в предыдущем туре.\n");
                                int bagSize = nextTourGamer.getBag().getItems().size();
                                if (bagSize == 0) {
                                    infoBuilder.append("У ").append(nextTourGamer.getNickname()).append(" нет предметов.\n");
                                } else {
                                    Random random = new Random();
                                    Item itemToShow = nextTourGamer.getBag().getItems().get(random.nextInt(bagSize));
                                    infoBuilder.append("У ").append(nextTourGamer.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".\n");
                                }
                            } else if (gamersWithMaxVotes.size() == 1) {
                                int bagSize = nextTourGamer.getBag().getItems().size();
                                if (bagSize == 0) {
                                    infoBuilder.append("У ").append(nextTourGamer.getNickname()).append(" нет предметов.\n");
                                } else {
                                    Random random = new Random();
                                    Item itemToShow = nextTourGamer.getBag().getItems().get(random.nextInt(bagSize));
                                    infoBuilder.append("У ").append(nextTourGamer.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".\n");
                                }
                                Gamer gamer = gamersWithMaxVotes.get(0);
                                infoBuilder.append("У ").append(gamer.getNickname()).append(" \"").append(gamer.getRole().getRoleColor().getColor()).append("\" цвет.");
                            } else if (gamersWithMaxVotes.size() > 1) {
                                infoBuilder.append("По результатам голосования игрокам снова не удалось договориться, поэтому будет показана информация об игроке, набравшем максимальное количество голосов в предыдущем туре.\n");
                                int bagSize = nextTourGamer.getBag().getItems().size();
                                if (bagSize == 0) {
                                    infoBuilder.append("У ").append(nextTourGamer.getNickname()).append(" нет предметов.\n");
                                } else {
                                    Random random = new Random();
                                    Item itemToShow = nextTourGamer.getBag().getItems().get(random.nextInt(bagSize));
                                    infoBuilder.append("У ").append(nextTourGamer.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".\n");
                                }
                            }
                        } else {
                            List<Gamer> gamersWithMaxVotes = new ArrayList<>();
                            List<Gamer> gamersWithSecondMaxVotes = new ArrayList<>();

                            StringBuilder textBuilder = new StringBuilder();

                            if (!votesCountMap.keySet().isEmpty()) {
                                textBuilder.append("Распределение голосов:\n");

                                int maxVotes = 0;
                                int secondMaxVotes = 0;

                                int totalPlayers = votesCountMap.keySet().size();
                                int count = 0;
                                for (Gamer gamer : votesCountMap.keySet()) {
                                    int currentVotes = votesCountMap.get(gamer);

                                    textBuilder.append(gamer.getNickname());
                                    textBuilder.append(" - ");
                                    textBuilder.append(currentVotes);
                                    if (++count < totalPlayers) {
                                        textBuilder.append("\n");
                                    }

                                    if (currentVotes > maxVotes) {
                                        secondMaxVotes = maxVotes;
                                        gamersWithSecondMaxVotes.clear();
                                        gamersWithSecondMaxVotes.addAll(gamersWithMaxVotes);

                                        maxVotes = currentVotes;
                                        gamersWithMaxVotes.clear();
                                        gamersWithMaxVotes.add(gamer);
                                    } else if (currentVotes == maxVotes) {
                                        gamersWithMaxVotes.add(gamer);
                                    } else if (currentVotes > secondMaxVotes) {
                                        secondMaxVotes = currentVotes;
                                        gamersWithSecondMaxVotes.clear();
                                        gamersWithSecondMaxVotes.add(gamer);
                                    } else if (currentVotes == secondMaxVotes) {
                                        gamersWithSecondMaxVotes.add(gamer);
                                    }
                                }
                            }

                            if (gamersWithMaxVotes.size() == 0) {
                                infoBuilder.append("По результатам дополнительного голосования не будет предпринято никаких действий.");
                            } else if (gamersWithMaxVotes.size() == 1) {
                                if (gamersWithSecondMaxVotes.size() == 0) {
                                    Gamer gamer = gamersWithMaxVotes.get(0);
                                    int bagSize = gamer.getBag().getItems().size();
                                    if (bagSize == 0) {
                                        infoBuilder.append("У ").append(gamer.getNickname()).append(" нет предметов.");
                                    } else {
                                        Random random = new Random();
                                        Item itemToShow = gamer.getBag().getItems().get(random.nextInt(bagSize));
                                        infoBuilder.append("У ").append(gamer.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".");
                                    }
                                } else if (gamersWithSecondMaxVotes.size() == 1) {
                                    Gamer gamer1 = gamersWithMaxVotes.get(0);
                                    int bagSize = gamer1.getBag().getItems().size();
                                    if (bagSize == 0) {
                                        infoBuilder.append("У ").append(gamer1.getNickname()).append(" нет предметов.\n");
                                    } else {
                                        Random random = new Random();
                                        Item itemToShow = gamer1.getBag().getItems().get(random.nextInt(bagSize));
                                        infoBuilder.append("У ").append(gamer1.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".\n");
                                    }
                                    Gamer gamer2 = gamersWithSecondMaxVotes.get(0);
                                    infoBuilder.append("У ").append(gamer2.getNickname()).append(" \"").append(gamer2.getRole().getRoleColor().getColor()).append("\" цвет.");
                                } else if (gamersWithSecondMaxVotes.size() > 2) {
                                    infoBuilder.append("По результатам дополнительного голосования игрокам удалось ограниченно договориться, поэтому будет показана информация только об игроке, набравшем максимальное количество голосов.");
                                    Gamer gamer = gamersWithMaxVotes.get(0);
                                    int bagSize = gamer.getBag().getItems().size();
                                    if (bagSize == 0) {
                                        infoBuilder.append("У ").append(gamer.getNickname()).append(" нет предметов.");
                                    } else {
                                        Random random = new Random();
                                        Item itemToShow = gamer.getBag().getItems().get(random.nextInt(bagSize));
                                        infoBuilder.append("У ").append(gamer.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".");
                                    }
                                }
                            } else if (gamersWithMaxVotes.size() == 2) {
                                Gamer gamer1 = gamersWithMaxVotes.get(0);
                                int bagSize = gamer1.getBag().getItems().size();
                                if (bagSize == 0) {
                                    infoBuilder.append("У ").append(gamer1.getNickname()).append(" нет предметов.\n");
                                } else {
                                    Random random = new Random();
                                    Item itemToShow = gamer1.getBag().getItems().get(random.nextInt(bagSize));
                                    infoBuilder.append("У ").append(gamer1.getNickname()).append(" найден предмет \"").append(itemToShow.getName()).append("\".\n");
                                }
                                Gamer gamer2 = gamersWithMaxVotes.get(1);
                                infoBuilder.append("У ").append(gamer2.getNickname()).append(" \"").append(gamer2.getRole().getRoleColor().getColor()).append("\" цвет.");
                            } else if (gamersWithMaxVotes.size() > 2) {
                                infoBuilder.append("По результатам дополнительного голосования не будет предпринято никаких действий.");
                            }
                        }
                        nextTourGamer = null;
                        nextTour = false;
                        nextTourParticipants.clear();
                        messagesToPlayers.add(infoBuilder.toString());
                        stage = GameStage.NIGHT;
                        gameSessionObserver.update(GameSession.this, GameSessionNotifyType.STAGE_CHANGED);
                        break;
                    case NIGHT:
                        messagesToPlayers.clear();
                        stage = GameStage.DISCUSSION;

                        gameSessionObserver.update(GameSession.this,GameSessionNotifyType.STAGE_CHANGED);

                        break;
                    case DISCUSSION:
                        stage = GameStage.VOTING;

                        gameSessionObserver.update(GameSession.this,GameSessionNotifyType.STAGE_CHANGED);

                        break;
                    case VOTING:
                        messagesToPlayers.clear();
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

    public void vote(Gamer voter, Gamer target) {
        //в дальнейшем количество добавляемых голосов будет вычислять по ценности голоса каждого игрока
        int currentVotes = votesCountMap.getOrDefault(target, 0);
        votesCountMap.put(target, currentVotes + 1);
    }
    public List<Gamer> getNotificationParticipants() {
        List<Gamer> notificationParticipants = new ArrayList<>();
        notificationParticipants.addAll(
                gamers
                        .stream()
                        .filter(g -> g.isInGame() && g.isCapable())
                        .collect(Collectors.toList())
        );
        return notificationParticipants;
    }

    public List<Gamer> getVotingTargets(Gamer voter) {
        if (nextTour) {
            return nextTourParticipants
                    .stream()
                    .filter(g -> g.getChatId() != voter.getChatId())
                    .collect(Collectors.toList());
        } else {
            List<Gamer> votingTargets = new ArrayList<>();
            if (voter.isAlive() && voter.isCapable()) {
                votingTargets = gamers
                        .stream()
                        .filter(g -> g.isAlive() && g.getChatId() != voter.getChatId())
                        .collect(Collectors.toList());
            }
            return votingTargets;
        }
    }

    public List<Gamer> getCommunicationParticipants(Gamer gamer) {
        List<Gamer> communicationParticipants = new ArrayList<>();
        if (gamer.isAlive() && gamer.isCapable()) {
            if (stage == GameStage.DISCUSSION
                    || stage == GameStage.VOTING
                    || stage == GameStage.FIRST_DISCUSSION
                    || stage == GameStage.FIRST_VOTING) {
                communicationParticipants.addAll(
                        gamers
                                .stream()
                                .filter(g -> g.isInGame() && g.getChatId()!=gamer.getChatId())
                                .collect(Collectors.toList())
                );
            }
        } else if (!gamer.isAlive()) {
            communicationParticipants.addAll(
                    gamers
                            .stream()
                            .filter(g -> !g.isAlive() && g.getChatId()!=gamer.getChatId())
                            .collect(Collectors.toList())
            );
        }
        return communicationParticipants;
    }

    public boolean isVotePublic() {
        return room.getRoomSettings().getVotingType() == RoomSettings.VotingType.PUBLIC;
    }
    public void addMessageToPlayers(String message) {
        messagesToPlayers.add(message);
    }

    public Queue<String> getMessagesToPlayers() {
        return messagesToPlayers;
    }

}
