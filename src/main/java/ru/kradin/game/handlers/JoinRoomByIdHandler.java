package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.game.exceptions.PlayerDoesNotExistException;
import ru.kradin.game.exceptions.RoomDoesNotExistException;
import ru.kradin.game.keyboards.BackKeyboard;
import ru.kradin.game.room.Room;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.RoomService;
import ru.kradin.game.services.TelegramBot;
import ru.kradin.game.utils.StateCreator;

@Component
public class JoinRoomByIdHandler implements InternalHandler {
    private static final String HANDLER_NAME = "join_room";
    private static final String ENTER_ID_LOCAL_STATE = "eid";
    private static final String SET_ID_LOCAL_STATE = "sid";

    private TelegramBot telegramBot;
    @Autowired
    private ChatStateService chatStateService;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    private RoomService roomService;
    @Override
    public void handle(Update update, String state) {
        String[] stateData = state.split(";");
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            if (update.getMessage().getText().equals(BackKeyboard.getBackText())) {
                state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
                internalHandlerSwitcher.switchHandler(update, state);
            }
            switch (stateData[1]) {
                case ENTER_ID_LOCAL_STATE:
                    SendMessage enterIdMessage = new SendMessage(String.valueOf(chatId),"Введите ID комнаты:");
                    BackKeyboard.setKeyboard(enterIdMessage);
                    chatStateService.setState(chatId,StateCreator.create(HANDLER_NAME,SET_ID_LOCAL_STATE));
                    telegramBot.sendMessage(enterIdMessage);
                    break;
                case SET_ID_LOCAL_STATE:
                    String roomId = update.getMessage().getText();
                    Room room;
                    try {
                        room = roomService.joinRoomByRoomIdAndPlayerChatId(roomId, chatId);
                        state = chatStateService.setState(chatId,InRoomHandler.getStateForRoomJoiner(roomId));
                        internalHandlerSwitcher.switchHandler(update, state);
                    } catch (RoomDoesNotExistException e) {
                        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Комната не найдена, попробуйте ещё раз:");
                        telegramBot.sendMessage(sendMessage);
                    } catch (PlayerDoesNotExistException e) {
                        state = chatStateService.setState(chatId, RegistrationHandler.getStateForStartingRegistration());
                        internalHandlerSwitcher.switchHandler(update, state);
                    }
                    break;
            }
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

    public static String getStateFroJoiningById() {
        return StateCreator.create(HANDLER_NAME, ENTER_ID_LOCAL_STATE);
    }
}
