package ru.kradin.murder_at_the_hotel.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kradin.murder_at_the_hotel.enums.GameSessionNotifyType;
import ru.kradin.murder_at_the_hotel.game.GameSession;
import ru.kradin.murder_at_the_hotel.game.GameSessionObserver;
import ru.kradin.murder_at_the_hotel.keyboards.MainMenuKeyboard;
import ru.kradin.murder_at_the_hotel.services.ChatStateService;
import ru.kradin.murder_at_the_hotel.services.TelegramBot;
import ru.kradin.murder_at_the_hotel.utils.StateCreator;

import java.util.*;

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
            sendMessage.setText("Игра не найдена.");
            MainMenuKeyboard.setKeyboard(sendMessage);

            telegramBot.sendMessage(sendMessage);

            state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenuWithoutMessage());
            internalHandlerSwitcher.switchHandler(update, state);
        }

        GameSession gameSession = gameSessionIdGameSessionMap.get(gameSessionId);
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

    public static SendMessage getControlMenuMessage(long chatId) {
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

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Игра началась!");
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }
}
