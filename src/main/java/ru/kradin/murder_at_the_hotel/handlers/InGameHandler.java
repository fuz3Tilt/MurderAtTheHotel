package ru.kradin.murder_at_the_hotel.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kradin.murder_at_the_hotel.enums.GameSessionNotifyType;
import ru.kradin.murder_at_the_hotel.enums.GameStage;
import ru.kradin.murder_at_the_hotel.game.GameSession;
import ru.kradin.murder_at_the_hotel.game.GameSessionObserver;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.abilities.Ability;
import ru.kradin.murder_at_the_hotel.game.items.Bag;
import ru.kradin.murder_at_the_hotel.game.items.Item;
import ru.kradin.murder_at_the_hotel.game.roles.Role;
import ru.kradin.murder_at_the_hotel.keyboards.MainMenuKeyboard;
import ru.kradin.murder_at_the_hotel.services.ChatStateService;
import ru.kradin.murder_at_the_hotel.services.TelegramBot;
import ru.kradin.murder_at_the_hotel.utils.StateCreator;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InGameHandler implements InternalHandler, GameSessionObserver {
    private static final String HANDLER_NAME = "in_game";
    private static final String GAME_SESSION_INFO_TEXT = "Об игре ℹ\uFE0F";
    private static final String ROLE_INFO_TEXT = "Роль \uD83E\uDDB9";
    private static final String BAG_INFO_TEXT = "Багаж \uD83D\uDC5C";
    private Map<String, GameSession> gameSessionIdGameSessionMap;
    private TelegramBot telegramBot;
    @Autowired
    private ChatStateService chatStateService;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;

    public InGameHandler() {
        gameSessionIdGameSessionMap = new HashMap<>();
    }

    @Override
    public void handle(Update update, String state) {
        long chatId;
        if (update.hasMessage())
            chatId = update.getMessage().getChatId();
        else
            chatId = update.getCallbackQuery().getMessage().getChatId();

        String[] stateData = state.split(";");
        String gameSessionId = stateData[1];

        if (!gameSessionIdGameSessionMap.containsKey(gameSessionId)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Игра не существует.");
            MainMenuKeyboard.setKeyboard(sendMessage);

            telegramBot.sendMessage(sendMessage);

            state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenuWithoutMessage());
            internalHandlerSwitcher.switchHandler(update, state);
            return;
        }

        GameSession gameSession = gameSessionIdGameSessionMap.get(gameSessionId);
        Gamer processingGamer = gameSession.getGamers()
                .stream()
                .filter(g -> g.getChatId() == chatId)
                .findAny()
                .orElse(null);

        if (update.hasMessage()) {
            String message = update.getMessage().getText();
            switch (message) {
                case GAME_SESSION_INFO_TEXT:
                    sendGameSessionInfoMessage(chatId,gameSession);
                    break;
                case ROLE_INFO_TEXT:
                    sendRoleInfoMessage(chatId, gameSession);
                    break;
                case BAG_INFO_TEXT:
                    sendBagInfoMessage(chatId, gameSession);
                    break;
                default:
                    if (gameSession.getStage() == GameStage.DISCUSSION
                            || gameSession.getStage() == GameStage.VOTING
                            || gameSession.getStage() == GameStage.FIRST_DISCUSSION
                            || gameSession.getStage() == GameStage.FIRST_VOTING) {
                        List<Gamer> gamersWithoutWriter = gameSession.getGamers()
                                .stream()
                                .filter(g -> g.getChatId()!=chatId)
                                .collect(Collectors.toList());
                        gamersWithoutWriter.forEach(g -> {
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(g.getChatId());
                            sendMessage.setText(processingGamer.getNickname()+": "+message);
                            telegramBot.sendMessage(sendMessage);
                        });
                    }
                    break;
            }
        } else if (update.hasCallbackQuery()) {

        }
    }

    @Override
    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }

    @Override
    public void update(GameSession gameSession, GameSessionNotifyType gameSessionNotifyType) {
        switch (gameSessionNotifyType) {
            case GAME_STARTED:
                gameSessionIdGameSessionMap.put(gameSession.getId(), gameSession);
                for (Gamer gamer: gameSession.getGamers()) {

                    sendGameSessionInfoMessage(gamer.getChatId(), gameSession);
                    sendRoleInfoMessage(gamer.getChatId(), gameSession);
                    sendBagInfoMessage(gamer.getChatId(), gameSession);

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(gamer.getChatId());
                    sendMessage.setText("Игра началась! Ознакомьтесь с ролью и предметами.");
                    sendMessage.setReplyMarkup(getControlMenuMarkup());
                    telegramBot.sendMessage(sendMessage);
                }
                break;
            case STAGE_CHANGED:
                switch (gameSession.getStage()) {
                    case FIRST_DISCUSSION:
                        for (Gamer gamer : gameSession.getGamers()) {
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(gamer.getChatId());
                            sendMessage.setText("Бла бла бла, убийство кровь кишки вся хуйня. Расскажите другими игрокам, чем вы можете быть полезны расследованию.");
                            telegramBot.sendMessage(sendMessage);
                        }
                        break;
                    case FIRST_VOTING:

                    case VOTING:
                        for (Gamer gamer : gameSession.getGamers()) {
                            // + сообщение с разметкой для голосования.
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(gamer.getChatId());
                            sendMessage.setText("Проголосуйте за самого подозрительного игрока.");
                            telegramBot.sendMessage(sendMessage);
                        }
                        break;
                    case NIGHT:

                        break;
                    case DISCUSSION:
                        for (Gamer gamer: gameSession.getGamers()) {

                            sendGameSessionInfoMessage(gamer.getChatId(), gameSession);
                            sendRoleInfoMessage(gamer.getChatId(), gameSession);
                            sendBagInfoMessage(gamer.getChatId(), gameSession);

                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(gamer.getChatId());
                            sendMessage.setText("Обсудите произошедшее ночью.");
                            sendMessage.setReplyMarkup(getControlMenuMarkup());
                            telegramBot.sendMessage(sendMessage);
                        }
                        break;
                }
                break;

            case GAME_ENDED:
                gameSessionIdGameSessionMap.remove(gameSession.getId());
                gameSession.getRoom().setSearchable();
                // код смены состотяния игроков
                break;
        }
    }

    @Override
    public boolean isGameSessionIdInUse(String gameSessionId) {
        return gameSessionIdGameSessionMap.containsKey(gameSessionId);
    }

    public static String getStateForEnteringHandler(String gameSessionId) {
        return StateCreator.create(HANDLER_NAME,gameSessionId);
    }

    private ReplyKeyboardMarkup getControlMenuMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();

        row1.add(GAME_SESSION_INFO_TEXT);
        keyboardRows.add(row1);

        row2.add(ROLE_INFO_TEXT);
        keyboardRows.add(row2);

        row3.add(BAG_INFO_TEXT);
        keyboardRows.add(row3);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        return keyboardMarkup;
    }

    private void sendGameSessionInfoMessage(long chatId, GameSession gameSession) {
        List<Gamer> gamers = gameSession.getGamers();
        StringBuilder textBuilder = new StringBuilder();
        int aliveGamers = (int) gamers.stream()
                .filter(Gamer::isAlive)
                .count();

        textBuilder.append("\uD83C\uDFAF: ");
        textBuilder.append(gameSession.getStage().getStage());
        textBuilder.append("\n\n");

        textBuilder.append("\uD83C\uDFAE: ").append("<i><b>").append(aliveGamers).append("/").append(gamers.size()).append("</b></i>").append("\n");

        for (int i = 0; i < gamers.size(); i++) {
            Gamer gamer = gamers.get(i);

            if (gamer.isAlive()) {
                textBuilder.append("\uD83D\uDE42 ");
            } else {
                textBuilder.append("\uD83D\uDE35 ");
            }

            textBuilder.append("<i><b>");
            textBuilder.append(gamer.getNickname());
            textBuilder.append("</b></i>");
            textBuilder.append(" ");

            if (gamer.isAlive()) {
                textBuilder.append("(жив)");
            } else {
                textBuilder.append("(мёртв)");
            }
            textBuilder.append("\n");
        }
        textBuilder.append("\n");
        textBuilder.append(gameSession.getRoom().getRoomSettings().getSpeedType().getEmoji()).append(": <i>").append(gameSession.getRoom().getRoomSettings().getSpeedType().getType()).append("</i>");
        textBuilder.append("\n");
        textBuilder.append(gameSession.getRoom().getRoomSettings().getVotingType().getEmoji()).append(": <i>").append(gameSession.getRoom().getRoomSettings().getVotingType().getType()).append("</i>");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textBuilder.toString());
        telegramBot.sendMessage(sendMessage);
    }

    private void sendRoleInfoMessage(long chatId, GameSession gameSession) {
        Gamer gamer = gameSession.getGamers()
                .stream()
                .filter(g -> g.getChatId() == chatId)
                .findAny()
                .orElse(null);

        Role role = gamer.getRole();

        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("\uD83E\uDDB9: ");
        textBuilder.append(role.getName());
        textBuilder.append("\n");
        textBuilder.append("ℹ\uFE0F: ");
        textBuilder.append(role.getDescription());

        textBuilder.append("\n\n");
        for (Ability ability: role.getAbilities()) {
            textBuilder.append("\uD83E\uDE84: ");
            textBuilder.append(ability.getName());
            textBuilder.append(" (");
            if (ability.isActive()) {
                textBuilder.append("активная");
            } else {
                textBuilder.append("пассивная");
            }
            textBuilder.append(")");
            textBuilder.append("\n");
            textBuilder.append("ℹ\uFE0F: ");
            textBuilder.append(ability.getDescription());
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textBuilder.toString());
        telegramBot.sendMessage(sendMessage);
    }

    private void sendBagInfoMessage(long chatId, GameSession gameSession) {
        Gamer gamer = gameSession.getGamers()
                .stream()
                .filter(g -> g.getChatId() == chatId)
                .findAny()
                .orElse(null);

        Bag bag = gamer.getBag();

        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i<bag.getItems().size();i++) {
            Item item = bag.getItems().get(i);
            textBuilder.append("\uD83D\uDD27: ");
            textBuilder.append(i+1).append(") ").append(item.getName());
            textBuilder.append("\n");
            textBuilder.append("ℹ\uFE0F: ").append(item.getDescription());
            textBuilder.append("\n\n");
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textBuilder.toString());
        telegramBot.sendMessage(sendMessage);
    }
}
