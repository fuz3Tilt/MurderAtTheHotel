package ru.kradin.murder_at_the_hotel.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kradin.murder_at_the_hotel.enums.SpecialLocalState;
import ru.kradin.murder_at_the_hotel.exceptions.PlayerDoesNotExistException;
import ru.kradin.murder_at_the_hotel.exceptions.RoomDoesNotExistException;
import ru.kradin.murder_at_the_hotel.room.Room;
import ru.kradin.murder_at_the_hotel.services.ChatStateService;
import ru.kradin.murder_at_the_hotel.services.RoomService;
import ru.kradin.murder_at_the_hotel.services.TelegramBot;
import ru.kradin.murder_at_the_hotel.utils.IdGenerator;
import ru.kradin.murder_at_the_hotel.utils.MessageIdUtil;
import ru.kradin.murder_at_the_hotel.utils.StateCreator;

import java.util.ArrayList;
import java.util.List;

@Component
public class PublicRoomsHandler implements InternalHandler{
    private static final String HANDLER_NAME = "public_rooms";
    private static final String UPDATE_KEY_TEXT = "Обновить \uD83D\uDD04";
    private static final String BACK_KEY_TEXT = "Назад ⬅\uFE0F";
    private static final String NAVIGATION_LOCAL_STATE = "n";
    private static final String JOIN_ROOM_LOCAL_STATE = "j";
    private TelegramBot telegramBot;
    @Autowired
    private RoomService roomService;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    private ChatStateService chatStateService;
    @Override
    public void handle(Update update, String state) {
        List<Room> publicRooms = roomService.getPublicRooms();
        if (update.hasMessage()) {
            String processingMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (processingMessage) {
                case BACK_KEY_TEXT:
                    state = chatStateService.setState(chatId,MainMenuHandler.getStateForGettingMainMenu());
                    internalHandlerSwitcher.switchHandler(update,state);
                    break;
                case UPDATE_KEY_TEXT:
                default:
                    if (processingMessage.equals(MainMenuHandler.getPublicRooms()) || processingMessage.equals(UPDATE_KEY_TEXT)) {
                        if (publicRooms.isEmpty()) {
                            SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Открытых комнат пока нет, вы можете обновить список или создать свою комнату.");
                            setUpdateBackKeyboard(sendMessage);
                            telegramBot.sendMessage(sendMessage);
                        } else {
                            int index = 0;
                            Room shownRoom = publicRooms.get(index);
                            String buttonsId = IdGenerator.generateForButton();

                            SendMessage messageWithKeyboard = new SendMessage(String.valueOf(chatId),"Открытые комнаты:");
                            setUpdateBackKeyboard(messageWithKeyboard);

                            SendMessage roomInfoMessageWithNavigationKeyboard = new SendMessage(String.valueOf(chatId),shownRoom.getInfo(0));
                            roomInfoMessageWithNavigationKeyboard.setReplyMarkup(getRoomsNavigationMarkupInLine(publicRooms,index,buttonsId));
                            chatStateService.setState(chatId,StateCreator.create(HANDLER_NAME,SpecialLocalState.EMPTY.name(),buttonsId));
                            telegramBot.sendMessage(messageWithKeyboard);
                            telegramBot.sendMessage(roomInfoMessageWithNavigationKeyboard);
                        }
                    }
                    break;
            }
            // обработка нажатых кнопок
        } else if (update.hasCallbackQuery()) {
            String[] callbackData = update.getCallbackQuery().getData().split(";");
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = MessageIdUtil.getMessageId(update.getCallbackQuery().getMessage());
            switch (callbackData[3]) {
                case NAVIGATION_LOCAL_STATE:
                    if (publicRooms.isEmpty()) {
                        EditMessageText publicRoomsNotFoundEditMessage = new EditMessageText();
                        publicRoomsNotFoundEditMessage.setChatId(chatId);
                        publicRoomsNotFoundEditMessage.setMessageId(messageId);
                        publicRoomsNotFoundEditMessage.setText("Открытых комнат пока нет, вы можете обновить список или создать свою комнату.");
                        telegramBot.editMessage(publicRoomsNotFoundEditMessage);
                        return;
                    }

                    String buttonsId = IdGenerator.generateForButton();

                    int indexToSwitch = Integer.valueOf(callbackData[4]);

                    if (indexToSwitch > publicRooms.size() - 1) {
                        indexToSwitch = publicRooms.size() - 1;
                    }

                    Room shownRoom = publicRooms.get(indexToSwitch);

                    EditMessageText roomInfo = new EditMessageText();
                    roomInfo.setChatId(chatId);
                    roomInfo.setMessageId(messageId);
                    roomInfo.setText(shownRoom.getInfo(0));
                    roomInfo.setReplyMarkup(getRoomsNavigationMarkupInLine(publicRooms,indexToSwitch, buttonsId));
                    chatStateService.setState(chatId,StateCreator.create(HANDLER_NAME,SpecialLocalState.EMPTY.name(),buttonsId));
                    telegramBot.editMessage(roomInfo);
                    break;
                case JOIN_ROOM_LOCAL_STATE:
                    try {
                    String roomId = callbackData[4];
                        Room room = roomService.joinRoomByRoomIdAndPlayerChatId(roomId,chatId);
                        state = InRoomHandler.getStateForRoomJoiner(roomId);
                        internalHandlerSwitcher.switchHandler(update, state);
                    } catch (RoomDoesNotExistException e) {
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setMessageId(messageId);
                        editMessageText.setText("Комната не найдена, обновите список.");
                        telegramBot.editMessage(editMessageText);
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

    public static String getSateForGettingPublicRooms() {
        return StateCreator.create(HANDLER_NAME, SpecialLocalState.EMPTY.name());
    }

    private void setUpdateBackKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        row1.add(UPDATE_KEY_TEXT);
        keyboardRows.add(row1);

        row2.add(BACK_KEY_TEXT);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    private InlineKeyboardMarkup getRoomsNavigationMarkupInLine(List<Room> rooms, int currentIndex, String buttonsId) {
        Room room = rooms.get(currentIndex);

        int shownIndex = currentIndex + 1;

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();

        var previousRoomButton = new InlineKeyboardButton();
        previousRoomButton.setText("<<");
        previousRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,SpecialLocalState.EMPTY.name(),buttonsId,NAVIGATION_LOCAL_STATE,String.valueOf(currentIndex-1)));

        var currentRoomButton = new InlineKeyboardButton();
        currentRoomButton.setText(shownIndex+"/"+rooms.size());
        currentRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,SpecialLocalState.EMPTY.name(),buttonsId));

        var nextRoomButton = new InlineKeyboardButton();
        nextRoomButton.setText(">>");
        nextRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,SpecialLocalState.EMPTY.name(),buttonsId,NAVIGATION_LOCAL_STATE,String.valueOf(currentIndex+1)));

        var joinRoomButton = new InlineKeyboardButton();
        joinRoomButton.setText("Войти в комнату");
        joinRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,SpecialLocalState.EMPTY.name(),buttonsId,JOIN_ROOM_LOCAL_STATE,room.getId()));

        if (shownIndex == 1 && rooms.size() == 1) {
            rowInLine1.add(currentRoomButton);
        } else if (shownIndex != 1 && shownIndex == rooms.size()) {
            rowInLine1.add(previousRoomButton);
            rowInLine1.add(currentRoomButton);
        } else if (shownIndex == 1 && rooms.size() > 1) {
            rowInLine1.add(currentRoomButton);
            rowInLine1.add(nextRoomButton);
        } else {
            rowInLine1.add(previousRoomButton);
            rowInLine1.add(currentRoomButton);
            rowInLine1.add(nextRoomButton);
        }
        rowInLine2.add(joinRoomButton);

        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);

        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }
}
