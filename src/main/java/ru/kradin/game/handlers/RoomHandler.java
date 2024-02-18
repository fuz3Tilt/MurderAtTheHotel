package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kradin.game.enums.RoomType;
import ru.kradin.game.enums.SpecialLocalState;
import ru.kradin.game.exceptions.PlayerDoesNotExistException;
import ru.kradin.game.exceptions.RoomDoesNotExistException;
import ru.kradin.game.keyboards.MainMenuKeyboard;
import ru.kradin.game.models.Player;
import ru.kradin.game.room.Room;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.RoomService;
import ru.kradin.game.services.TelegramBot;
import ru.kradin.game.utils.IdGenerator;
import ru.kradin.game.utils.MessageIdUtil;
import ru.kradin.game.utils.StateCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomHandler implements InternalHandler {
    private static enum NotifyType{ROOM_UPDATE,CHAT};
    private static final String HANDLER_NAME = "room";
    private static final String GET_PUBLIC_ROOMS = "gprs";
    private static final String CREATE_PUBLIC_ROOM = "cpbr";
    private static final String CREATE_PRIVATE_ROOM = "cprr";
    private static final String JOIN_ROOM_BY_ID = "jrbid";
    private static final String IN_ROOM = "inr";
    private static final String ENTER_ID = "eid";
    private static final String NAVIGATION = "n";
    private TelegramBot telegramBot;
    private static final String ABOUT_ROOM = "О комнате ℹ\uFE0F";
    private static final String EXIT = "Выйти \uD83D\uDEAA";
    private static final String BACK = "Назад ⬅\uFE0F";
    private static final String UPDATE = "Обновить \uD83D\uDD04";
    private static final String KICK = "Выгнать";
    private static final String ROOM_CREATED = "Вы создали комнату.";
    private static final String ROOM_DOES_NOT_EXIST = "Комната не существует.";
    private static final String PLAYER_LEFT = "Вы вышли из комнаты.";
    private static final String PLAYER_JOINED = "Вы вошли в комнату.";
    private static final String TEXT_FOR_ROOM_INFO = "\n\nВы можете общаться друг с другом с помощью бота!";
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    private ChatStateService chatStateService;
    @Autowired
    private RoomService roomService;
    @Override
    public void handle(Update update, String state) {
        String[] data = state.split(";");
        String methodName = data[1];
        switch (methodName) {
            case GET_PUBLIC_ROOMS:
                getPublicRooms(update, state);
                break;
            case CREATE_PUBLIC_ROOM:
                createRoom(update, RoomType.PUBLIC);
                break;
            case CREATE_PRIVATE_ROOM:
                createRoom(update, RoomType.PRIVATE);
                break;
            case JOIN_ROOM_BY_ID:
                joinRoomById(update, state);
                break;
            case IN_ROOM:
                inRoom(update, state);
                break;
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

    public static String getStateForGettingPublicRooms() {
        return StateCreator.create(HANDLER_NAME,GET_PUBLIC_ROOMS);
    }

    public static String getStateForCreatingPublicRoom() {
        return StateCreator.create(HANDLER_NAME,CREATE_PUBLIC_ROOM);
    }

    public static String getStateForCreatingPrivateRoom() {
        return StateCreator.create(HANDLER_NAME,CREATE_PRIVATE_ROOM);
    }

    public static String getStateForJoiningRoomById() {
        return StateCreator.create(HANDLER_NAME,JOIN_ROOM_BY_ID);
    }

    private void getPublicRooms(Update update, String state) {
        long chatId;
        List<Room> rooms = roomService.getPublicRooms();
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            switch (update.getMessage().getText()) {
                case BACK:
                    state = chatStateService.setState(chatId,MainMenuHandler.getStateForGettingMainMenu());
                    internalHandlerSwitcher.switchHandler(update,state);
                    break;
                case UPDATE:
                default:
                    if (update.getMessage().getText().equals(MainMenuHandler.getPublicRooms()) || update.getMessage().getText().equals(UPDATE)) {
                        if (rooms.size() != 0) {
                            Room room = rooms.get(0);
                            String buttonsId = IdGenerator.generateForButton();
                            SendMessage publicRooms = new SendMessage(String.valueOf(chatId), "Открытые комнаты:");
                            SendMessage roomInfo = new SendMessage(String.valueOf(chatId), room.toString());
                            setUpdateBackKeyboard(publicRooms);
                            roomInfo.setReplyMarkup(getRoomsNavigationMarkupInLine(rooms, 0, buttonsId));
                            chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME, GET_PUBLIC_ROOMS, buttonsId));
                            telegramBot.sendMessage(publicRooms);
                            telegramBot.sendMessage(roomInfo);
                        } else {
                            sendPublicRoomsNotFoundMessage(chatId);
                        }
                    }
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            String[] data = update.getCallbackQuery().getData().split(";");
            String buttonsId = IdGenerator.generateForButton();
            String callbackType = data[3];
            switch (callbackType) {
                case NAVIGATION:
                    String targetIndex = data[4];
                    int index = Integer.parseInt(targetIndex);
                    if (index >= 0 && index < rooms.size()) {
                        Room room = rooms.get(index);
                        EditMessageText roomInfo = new EditMessageText();
                        roomInfo.setChatId(chatId);
                        roomInfo.setText(room.toString());
                        roomInfo.setMessageId(MessageIdUtil.getMessageId(update.getCallbackQuery().getMessage()));
                        roomInfo.setReplyMarkup(getRoomsNavigationMarkupInLine(rooms, index, buttonsId));
                        chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME, GET_PUBLIC_ROOMS, buttonsId));
                        telegramBot.editMessage(roomInfo);
                    } else if (rooms.size() != 0){
                        Room room = rooms.get(rooms.size()-1);
                        EditMessageText roomInfo = new EditMessageText();
                        roomInfo.setChatId(chatId);
                        roomInfo.setText(room.toString());
                        roomInfo.setMessageId(MessageIdUtil.getMessageId(update.getCallbackQuery().getMessage()));
                        roomInfo.setReplyMarkup(getRoomsNavigationMarkupInLine(rooms, index, buttonsId));
                        chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME, GET_PUBLIC_ROOMS, buttonsId));
                        telegramBot.editMessage(roomInfo);
                    } else {
                        sendPublicRoomsNotFoundMessage(chatId);
                    }
                    break;
                case JOIN_ROOM_BY_ID:
                    String roomId = data[4];
                    try {
                        Room room = roomService.joinRoomByRoomIdAndPlayerChatId(roomId, chatId);
                        state = chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME, IN_ROOM, room.getId()));
                        Player actor = (Player) room.getPlayers().stream().filter(c -> {
                            return c.getChatId() == chatId;
                        }).toArray()[0];
                        notifyPlayers(room, actor, NotifyType.ROOM_UPDATE, actor.getNickname()+" вошёл в комнату.");
                        EditMessageText roomInfo = new EditMessageText();
                        roomInfo.setChatId(chatId);
                        roomInfo.setText(room.toString()+TEXT_FOR_ROOM_INFO);
                        roomInfo.setMessageId(MessageIdUtil.getMessageId(update.getCallbackQuery().getMessage()));
                        SendMessage messageToPlayer = new SendMessage(String.valueOf(chatId), PLAYER_JOINED);
                        setRoomKeyboard(messageToPlayer);
                        telegramBot.editMessage(roomInfo);
                        telegramBot.sendMessage(messageToPlayer);
                    } catch (RoomDoesNotExistException e) {
                        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Комната не найдена, нажмите "+"\""+UPDATE+"\" или выберете другую комнату.");
                        telegramBot.sendMessage(sendMessage);
                    } catch (PlayerDoesNotExistException e) {
                        state = chatStateService.setState(chatId, RegistrationHandler.getStateForStartingRegistration());
                        internalHandlerSwitcher.switchHandler(update, state);
                    }
                    break;
            }
        }
    }

    private void createRoom(Update update, RoomType roomType) {
        long chatId = update.getMessage().getChatId();
        try {
            Room room = roomService.createRoomByChatIdAndRoomType(chatId, roomType);
            String state = StateCreator.create(HANDLER_NAME,IN_ROOM,room.getId());

            SendMessage roomCreatedMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), ROOM_CREATED);
            setRoomKeyboard(roomCreatedMessage);
            SendMessage roomInfoMessage = getRoomInfoMessage(update, state);
            telegramBot.sendMessage(roomInfoMessage);
            telegramBot.sendMessage(roomCreatedMessage);
            chatStateService.setState(chatId, state);
            internalHandlerSwitcher.switchHandler(update, state);
        } catch (PlayerDoesNotExistException e) {
            String state = chatStateService.setState(chatId, RegistrationHandler.getStateForStartingRegistration());
            internalHandlerSwitcher.switchHandler(update,state);
        }
        catch (RoomDoesNotExistException e) {
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId),ROOM_DOES_NOT_EXIST);
            telegramBot.sendMessage(sendMessage);
            String state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
            internalHandlerSwitcher.switchHandler(update, state);
        }
    }

    private void joinRoomById(Update update, String state) {
        String[] data = state.split(";");
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            if (data.length == 2) {
                chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME, JOIN_ROOM_BY_ID, ENTER_ID));
                SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Введите ID комнаты:");
                setBackKeyboard(sendMessage);
                telegramBot.sendMessage(sendMessage);
            } else if (data.length > 2 && data[2].equals(ENTER_ID)) {
                String message = update.getMessage().getText();
                switch (message) {
                    case BACK:
                        state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
                        internalHandlerSwitcher.switchHandler(update, state);
                        break;
                    default:
                        try {
                            Room room = roomService.joinRoomByRoomIdAndPlayerChatId(message, chatId);
                            state = chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME, IN_ROOM, room.getId()));
                            Player actor = (Player) room.getPlayers().stream().filter(c -> {
                                return c.getChatId() == chatId;
                            }).toArray()[0];
                            notifyPlayers(room, actor, NotifyType.ROOM_UPDATE, actor.getNickname() + " вошёл в комнату.");
                            SendMessage roomInfo = getRoomInfoMessage(update, state);
                            SendMessage messageToPlayer = new SendMessage(String.valueOf(chatId), PLAYER_JOINED);
                            setRoomKeyboard(messageToPlayer);
                            telegramBot.sendMessage(roomInfo);
                            telegramBot.sendMessage(messageToPlayer);
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
    }

    private void inRoom(Update update, String state) {
        if (update.hasMessage()) {
            String[] data = state.split(";");
            String roomId = data[2];
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            try {
                Room room = roomService.getRoomByRoomId(roomId);
                Player actor = (Player) room.getPlayers().stream().filter(c -> {
                    return c.getChatId() == chatId;
                }).toArray()[0];
                switch (messageText) {
                    case ABOUT_ROOM:
                        telegramBot.sendMessage(getRoomInfoMessage(update, state));
                        break;
                    case EXIT:
                        room.removePlayer(actor);
                        SendMessage messageToPlayer = new SendMessage(String.valueOf(chatId), PLAYER_LEFT);
                        telegramBot.sendMessage(messageToPlayer);
                        state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
                        internalHandlerSwitcher.switchHandler(update, state);
                        String notificationMessage = actor.getNickname() + " вышел из комнаты.";
                        notifyPlayers(room, actor, NotifyType.ROOM_UPDATE, notificationMessage);
                        break;
                    default:
                        String playerMessage = actor.getNickname()+": "+messageText;
                        notifyPlayers(room,actor,NotifyType.CHAT,playerMessage);
                        break;
                }
            } catch (RoomDoesNotExistException e) {
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId), ROOM_DOES_NOT_EXIST);
            telegramBot.sendMessage(sendMessage);
            state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
            internalHandlerSwitcher.switchHandler(update, state);
            }
        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String[] buttonData = update.getCallbackQuery().getData().split(";");
            String[] stateData = state.split(";");
            String roomId = stateData[2];
            String action = buttonData[3];
            try {
                switch (action) {
                    case KICK:
                        long targetChatId = Long.parseLong(buttonData[4]);
                        Room room = roomService.getRoomByRoomId(roomId);

                        Player actor = (Player) room.getPlayers().stream().filter(c -> {
                            return c.getChatId() == chatId;
                        }).toArray()[0];
                        Player target = room.getPlayers().stream()
                                .filter(c -> c.getChatId() == targetChatId)
                                .findFirst()
                                .orElse(null);

                        if (target == null)
                            return;

                        room.removePlayer(target);
                        SendMessage messageToTarget = new SendMessage(String.valueOf(targetChatId),"Вас выгнали из комнаты.");
                        MainMenuKeyboard.setKeyboard(messageToTarget);
                        chatStateService.setState(targetChatId,MainMenuHandler.getStateForGettingMainMenu());
                        notifyPlayers(room, target, NotifyType.ROOM_UPDATE, actor.getNickname() + " выгнал " + target.getNickname()+".");
                        telegramBot.sendMessage(messageToTarget);
                        break;
                }
            } catch (RoomDoesNotExistException e) {
                SendMessage sendMessage = new SendMessage(String.valueOf(chatId), ROOM_DOES_NOT_EXIST);
                telegramBot.sendMessage(sendMessage);
                state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
                internalHandlerSwitcher.switchHandler(update, state);
            }
        }
    }

    private SendMessage getRoomInfoMessage(Update update, String state) throws RoomDoesNotExistException {
        String[] data = state.split(";");
        String roomId = data[2];
        long chatId = 0;
        if (update.hasMessage())
            chatId = update.getMessage().getChatId();
        if (update.hasCallbackQuery())
            chatId = update.getCallbackQuery().getMessage().getChatId();

        Room room = roomService.getRoomByRoomId(roomId);

        String text = room.toString()+TEXT_FOR_ROOM_INFO;
        SendMessage roomInfoMessage = new SendMessage(String.valueOf(chatId), text);

        if (room.getOwner().getChatId() == chatId) {
            roomInfoMessage.setReplyMarkup(getRoomOwnerInlineKeyboard(room));
        }

        return roomInfoMessage;
    }

    private void setRoomKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        row1.add(ABOUT_ROOM);
        keyboardRows.add(row1);

        row2.add(EXIT);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    private void setBackKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();

        row1.add(BACK);
        keyboardRows.add(row1);

        keyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }
    
    private void setUpdateBackKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        row1.add(UPDATE);
        keyboardRows.add(row1);

        row2.add(BACK);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    private InlineKeyboardMarkup getRoomsNavigationMarkupInLine(List<Room> rooms, int targetIndex, String buttonsId) {
        Room room = rooms.get(targetIndex);

        int navigationTargetIndex = targetIndex + 1;

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();

        var previousRoomButton = new InlineKeyboardButton();
        previousRoomButton.setText("<<");
        previousRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,GET_PUBLIC_ROOMS,buttonsId,NAVIGATION,String.valueOf(navigationTargetIndex-2)));

        var currentRoomButton = new InlineKeyboardButton();
        currentRoomButton.setText(navigationTargetIndex+"/"+rooms.size());
        currentRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,SpecialLocalState.EMPTY.name(),buttonsId));

        var nextRoomButton = new InlineKeyboardButton();
        nextRoomButton.setText(">>");
        nextRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,GET_PUBLIC_ROOMS,buttonsId,NAVIGATION,String.valueOf(navigationTargetIndex)));

        var joinRoomButton = new InlineKeyboardButton();
        joinRoomButton.setText("Войти в комнату");
        joinRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,GET_PUBLIC_ROOMS,buttonsId,JOIN_ROOM_BY_ID,room.getId()));

        if (navigationTargetIndex == 1 && rooms.size() == 1) {
            rowInLine1.add(currentRoomButton);
        } else if (navigationTargetIndex != 1 && navigationTargetIndex == rooms.size()) {
            rowInLine1.add(previousRoomButton);
            rowInLine1.add(currentRoomButton);
        } else if (navigationTargetIndex == 1 && rooms.size() > 1) {
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

    private InlineKeyboardMarkup getRoomOwnerInlineKeyboard(Room room) {
        List<Player> playerListWithoutOwner = getPlayerListWithoutTarget(room.getPlayers(),room.getOwner());

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        for (Player player:playerListWithoutOwner) {
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var kickButton = new InlineKeyboardButton();
            kickButton.setText(KICK+" "+player.getNickname());
            kickButton.setCallbackData(StateCreator.create(HANDLER_NAME,IN_ROOM,room.getId(),KICK,String.valueOf(player.getChatId())));
            rowInLine.add(kickButton);

            rowsInLine.add(rowInLine);
        }
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    private List<Player> getPlayerListWithoutTarget(List<Player> players, Player target) {
        List<Player> withoutOwner = new ArrayList<>();
        for (Player player: players) {
            if (!player.equals(target))
                withoutOwner.add(player);
        }
        return withoutOwner;
    }

    private void notifyPlayers(Room room, Player notNotifingPlayer, NotifyType notifyType, String text) {
        List<Player> playerListWithoutActor = getPlayerListWithoutTarget(room.getPlayers(),notNotifingPlayer);
        for (Player player:playerListWithoutActor) {
            SendMessage notification = null;
            switch (notifyType) {
                case ROOM_UPDATE:
                    notification = new SendMessage(String.valueOf(player.getChatId()),text);
                    SendMessage roomInfo = new SendMessage(String.valueOf(player.getChatId()),room.toString()+TEXT_FOR_ROOM_INFO);

                    if (player.equals(room.getOwner())) {
                        roomInfo.setReplyMarkup(getRoomOwnerInlineKeyboard(room));
                    }

                    telegramBot.sendMessage(roomInfo);
                    telegramBot.sendMessage(notification);
                    break;
                case CHAT:
                    notification = new SendMessage(String.valueOf(player.getChatId()),text);
                    telegramBot.sendMessage(notification);
                    break;
            }
        }
    }

    private void sendPublicRoomsNotFoundMessage(long chatId) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "Открытых комнат пока нет, вы можете обновить список или создать свою комнату.");
        setUpdateBackKeyboard(sendMessage);
        telegramBot.sendMessage(sendMessage);
    }
}
