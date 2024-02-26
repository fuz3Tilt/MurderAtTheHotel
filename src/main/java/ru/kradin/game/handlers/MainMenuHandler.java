package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.game.enums.SpecialLocalState;
import ru.kradin.game.keyboards.MainMenuKeyboard;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.TelegramBot;
import ru.kradin.game.utils.StateCreator;

@Component
public class MainMenuHandler implements InternalHandler{
    private static final String HANDLER_NAME = "Главное меню \uD83D\uDCF1";
    private static final String PUBLIC_ROOMS = "Открытые комнаты \uD83C\uDFAE";
    private static final String CREATE_ROOM = "Создать комнату \uD83D\uDD11";
    private static final String JOIN_BY_ID = "Присоединиться по ID \uD83D\uDD10";
    private static final String CHANGE_NICKNAME = "Изменить никнейм ⚙\uFE0F";
    private static final String DO_NOT_SEND_MAIN_MENU_AGAIN = "DNSMMA";
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
                    state = chatStateService.setState(chatId,PublicRoomsHandler.getSateForGettingPublicRooms());
                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                case CREATE_ROOM:
                    state = chatStateService.setState(chatId,RoomCreatorHandler.getStateForCreatingRoom());
                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                case JOIN_BY_ID:
                    state = chatStateService.setState(chatId,JoinRoomByIdHandler.getStateFroJoiningById());
                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                case CHANGE_NICKNAME:
                    state = chatStateService.setState(chatId,ChangeNicknameHandler.getStateForChangingNickname());
                    internalHandlerSwitcher.switchHandler(update, state);
                    break;
                default:
                    String[] stateData = state.split(";");
                    if (stateData[1].equals(DO_NOT_SEND_MAIN_MENU_AGAIN))
                        return;

                    sendMainMenuKeyboard(chatId);
                    chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME,DO_NOT_SEND_MAIN_MENU_AGAIN));
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

    public static String getStateForGettingMainMenuWithoutMessage() {
        return StateCreator.create(HANDLER_NAME,DO_NOT_SEND_MAIN_MENU_AGAIN);
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

    public static String getCreateRoom() {
        return CREATE_ROOM;
    }

    public static String getJoinById() {
        return JOIN_BY_ID;
    }

    public static String getChangeNickname() {
        return CHANGE_NICKNAME;
    }

}
