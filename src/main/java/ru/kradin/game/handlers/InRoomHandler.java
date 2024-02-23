package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kradin.game.enums.SpecialLocalState;
import ru.kradin.game.exceptions.PlayerDoesNotExistException;
import ru.kradin.game.exceptions.RoomDoesNotExistException;
import ru.kradin.game.keyboards.MainMenuKeyboard;
import ru.kradin.game.models.Player;
import ru.kradin.game.room.Room;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.PlayerService;
import ru.kradin.game.services.RoomService;
import ru.kradin.game.services.TelegramBot;
import ru.kradin.game.utils.IdGenerator;
import ru.kradin.game.utils.StateCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InRoomHandler implements InternalHandler{
    private static final String HANDLER_NAME = "in_room";
    private static final String ROOM_CREATED_LOCAL_STATE = "rc";
    private static final String PLAYER_JOINED_LOCAL_STATE = "pj";
    private static final String KICK_LOCAL_STATE = "k";
    private static final String ABOUT_ROOM_KEY_TEXT = "О комнате ℹ\uFE0F";
    private static final String EXIT_KEY_TEXT = "Выйти \uD83D\uDEAA";
    private static final String TEXT_FOR_ROOM_INFO = "\n\nВы можете общаться друг с другом с помощью бота!";
    private TelegramBot telegramBot;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    private ChatStateService chatStateService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private PlayerService playerService;

    @Override
    public void handle(Update update, String state) {
        long chatId;
        if (update.hasMessage())
            chatId = update.getMessage().getChatId();
        else
            chatId = update.getCallbackQuery().getMessage().getChatId();

        String[] stateData = state.split(";");
        String roomId = stateData[1];
        try {
            Room room = roomService.getRoomByRoomId(roomId);
            Player processingPlayer = playerService.getByChatId(chatId);
            switch (stateData[2]) {
                case ROOM_CREATED_LOCAL_STATE:
                    notifyPlayers(room, processingPlayer.getNickname() + " создал комнату.");
                    return;
                case PLAYER_JOINED_LOCAL_STATE:
                    notifyPlayers(room, processingPlayer.getNickname() + " вошёл в комнату.");
                    chatStateService.setState(processingPlayer.getChatId(), StateCreator.create(HANDLER_NAME,room.getId(),SpecialLocalState.EMPTY.name()));
                    return;
            }
            if (update.hasMessage()) {
                switch (update.getMessage().getText()) {
                    case ABOUT_ROOM_KEY_TEXT:
                        SendMessage roomInfo = new SendMessage(String.valueOf(chatId), room.toString()+TEXT_FOR_ROOM_INFO);
                        // если игрок, действие которого обрабатывается, владелец комнаты, добавляем клавиатуру
                        if (processingPlayer.equals(room.getOwner())) {
                            String buttonsId = IdGenerator.generateForButton();
                            roomInfo.setReplyMarkup(getOwnerInlineMarkup(room, buttonsId));
                            chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME,room.getId(),buttonsId));
                        }
                        telegramBot.sendMessage(roomInfo);
                        break;
                    case EXIT_KEY_TEXT:
                        room.removePlayer(processingPlayer);
                        state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
                        internalHandlerSwitcher.switchHandler(update, state);
                        notifyPlayers(room, processingPlayer.getNickname() + " вышел из комнаты.");
                        break;
                    default:
                        // сообщение в чат всем игрокам
                        String text = update.getMessage().getText();

                        List<Player> playersWithoutWriter = room.getPlayers().stream()
                                .filter(p -> p.getChatId()!=chatId)
                                .collect(Collectors.toList());
                        playersWithoutWriter.forEach(p -> {
                            SendMessage message = new SendMessage(String.valueOf(p.getChatId()),processingPlayer.getNickname()+": "+text);
                            telegramBot.sendMessage(message);
                        });
                        break;
                }
            } else if (update.hasCallbackQuery()) {
                String[] callbackData = update.getCallbackQuery().getData().split(";");
                switch (callbackData[3]) {
                    case KICK_LOCAL_STATE:
                        long targetChatId = Long.parseLong(callbackData[4]);
                        Player kickedPlayer = room.getPlayers().stream()
                                .filter(p -> p.getChatId() == targetChatId)
                                .findAny()
                                .orElse(null);

                        if (kickedPlayer == null)
                            return;

                        room.removePlayer(kickedPlayer);

                        SendMessage messageToKickedPlayer = new SendMessage(String.valueOf(kickedPlayer.getChatId()), processingPlayer.getNickname() + " выгнал вас из комнаты.");
                        MainMenuKeyboard.setKeyboard(messageToKickedPlayer);

                        String messageToPlayers = processingPlayer.getNickname() + " выгнал "+kickedPlayer.getNickname()+" из комнаты.";
                        notifyPlayers(room,messageToPlayers);

                        chatStateService.setState(kickedPlayer.getChatId(), MainMenuHandler.getStateForGettingMainMenu());
                        telegramBot.sendMessage(messageToKickedPlayer);
                        break;
                }
            }
        } catch (RoomDoesNotExistException e) {
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId),"Комната не существует.");
            telegramBot.sendMessage(sendMessage);
            state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
            internalHandlerSwitcher.switchHandler(update, state);
        } catch (PlayerDoesNotExistException e) {
            state = chatStateService.setState(chatId, RegistrationHandler.getStateForStartingRegistration());
            internalHandlerSwitcher.switchHandler(update, state);
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

    public static String getStateForRoomCreator(String roomId) {
        return StateCreator.create(HANDLER_NAME,roomId,ROOM_CREATED_LOCAL_STATE);
    }

    public static String getStateForRoomJoiner(String roomId) {
        return StateCreator.create(HANDLER_NAME,roomId,PLAYER_JOINED_LOCAL_STATE);
    }

    private InlineKeyboardMarkup getOwnerInlineMarkup(Room room, String buttonsId) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        for (Player player:room.getPlayers()) {
            if (!player.equals(room.getOwner())) {
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var kickButton = new InlineKeyboardButton();
                kickButton.setText("Выгнать " + player.getNickname());
                kickButton.setCallbackData(StateCreator.create(HANDLER_NAME, room.getId(), buttonsId, KICK_LOCAL_STATE, String.valueOf(player.getChatId())));
                rowInLine.add(kickButton);

                rowsInLine.add(rowInLine);
            }
        }
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    private void setRoomKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        row1.add(ABOUT_ROOM_KEY_TEXT);
        keyboardRows.add(row1);

        row2.add(EXIT_KEY_TEXT);
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    private void notifyPlayers(Room room, String text) {
        for (Player player:room.getPlayers()) {
            SendMessage roomInfo = new SendMessage(String.valueOf(player.getChatId()), room.toString()+TEXT_FOR_ROOM_INFO);
            SendMessage message = new SendMessage(String.valueOf(player.getChatId()),text);
            setRoomKeyboard(message);
            if (player.equals(room.getOwner())) {
                String buttonsId = IdGenerator.generateForButton();
                roomInfo.setReplyMarkup(getOwnerInlineMarkup(room,buttonsId));
                chatStateService.setState(player.getChatId(), StateCreator.create(HANDLER_NAME,room.getId(),buttonsId));
            }
            telegramBot.sendMessage(roomInfo);
            telegramBot.sendMessage(message);
        }
    }
}
