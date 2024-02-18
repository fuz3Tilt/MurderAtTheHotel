package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kradin.game.enums.SpecialLocalState;
import ru.kradin.game.keyboards.MainMenuKeyboard;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.TelegramBot;
import ru.kradin.game.utils.StateCreator;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainMenuHandler implements InternalHandler{
    private static final String HANDLER_NAME = "Главное меню \uD83D\uDCF1";
    private static final String PUBLIC_ROOMS = "Открытые комнаты \uD83C\uDFAE";
    private static final String CREATE_PUBLIC_ROOM = "Создать открытую комнату \uD83D\uDD13";
    private static final String CREATE_PRIVATE_ROOM = "Создать закрытую комнату \uD83D\uDD12";
    private static final String JOIN_BY_ID = "Присоединиться по ID \uD83D\uDD10";
    private static final String CHANGE_NICKNAME = "Изменить никнейм ⚙\uFE0F";
    private TelegramBot telegramBot;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    private ChatStateService chatStateService;

    @Override
    public void handle(Update update, String state) {
        long chatId;

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            switch (update.getMessage().getText()) {
                case PUBLIC_ROOMS:
                    state = chatStateService.setState(chatId,RoomHandler.getStateForGettingPublicRooms());
                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                case CREATE_PUBLIC_ROOM:
                    state = chatStateService.setState(chatId,RoomHandler.getStateForCreatingPublicRoom());
                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                case CREATE_PRIVATE_ROOM:
                    state = chatStateService.setState(chatId,RoomHandler.getStateForCreatingPrivateRoom());
                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                case JOIN_BY_ID:
                    state = chatStateService.setState(chatId,RoomHandler.getStateForJoiningRoomById());
                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                case CHANGE_NICKNAME:
//                    state = chatStateService.setState();
//                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                default:
                    sendMainMenuKeyboard(chatId);
                    break;
            }
        } else if (update.hasCallbackQuery()){
            chatId = update.getCallbackQuery().getMessage().getChatId();
            sendMainMenuKeyboard(chatId);
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

    public static String getStateForGettingMainMenu() {
        return StateCreator.create(HANDLER_NAME,SpecialLocalState.EMPTY.toString());
    }

    private void sendMainMenuKeyboard(long chatId) {
        String text = HANDLER_NAME;
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId),text);
        MainMenuKeyboard.setKeyboard(sendMessage);
        telegramBot.sendMessage(sendMessage);
    }

    public static String getPublicRooms() {
        return PUBLIC_ROOMS;
    }

    public static String getCreatePublicRoom() {
        return CREATE_PUBLIC_ROOM;
    }

    public static String getCreatePrivateRoom() {
        return CREATE_PRIVATE_ROOM;
    }

    public static String getJoinById() {
        return JOIN_BY_ID;
    }

    public static String getChangeNickname() {
        return CHANGE_NICKNAME;
    }

}
