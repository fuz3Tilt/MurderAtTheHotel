package ru.kradin.murder_at_the_hotel.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.kradin.murder_at_the_hotel.enums.SpecialLocalState;
import ru.kradin.murder_at_the_hotel.exceptions.PlayerDoesNotExistException;
import ru.kradin.murder_at_the_hotel.keyboards.BackKeyboard;
import ru.kradin.murder_at_the_hotel.room.Room;
import ru.kradin.murder_at_the_hotel.room.RoomSettings;
import ru.kradin.murder_at_the_hotel.services.ChatStateService;
import ru.kradin.murder_at_the_hotel.services.RoomService;
import ru.kradin.murder_at_the_hotel.services.TelegramBot;
import ru.kradin.murder_at_the_hotel.utils.IdGenerator;
import ru.kradin.murder_at_the_hotel.utils.MessageIdUtil;
import ru.kradin.murder_at_the_hotel.utils.StateCreator;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoomCreatorHandler implements InternalHandler{
    private static final String HANDLER_NAME = "room_creator";
    private static final String DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE = "dnssa";
    private static final String CREATE_ROOM_LOCAL_STATE = "cr";
    private static final String PUBLIC_ACCESS_SETTINGS = "pu";
    private static final String PRIVATE_ACCESS_SETTINGS = "pr";
    private static final String NORMAL_SPEED_SETTINGS = "ns";
    private static final String FAST_SPEED_SETTINGS = "fs";
    private static final String PUBLIC_VOTING_SETTINGS = "pv";
    private static final String SECRET_VOTING_SETTINGS = "sv";
    private TelegramBot telegramBot;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    private ChatStateService chatStateService;
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
            } else if (!stateData[1].equals(DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE)) {
                SendMessage backKeyboardMessage = new SendMessage(String.valueOf(chatId), "Настройки:");
                BackKeyboard.setKeyboard(backKeyboardMessage);

                String buttonsId = IdGenerator.generateForButton();
                RoomSettings roomSettings = getInitialSettings();
                SendMessage settingsMessage = new SendMessage();
                settingsMessage.setChatId(chatId);
                settingsMessage.setText(roomSettings.toString());
                settingsMessage.setReplyMarkup(getSettingsMarkup(roomSettings, buttonsId));

                telegramBot.sendMessage(backKeyboardMessage);
                telegramBot.sendMessage(settingsMessage);
                chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId));
            }
        }
        // обработка нажатых кнопок
        else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = MessageIdUtil.getMessageId(update.getCallbackQuery().getMessage());
            String[] callbackData = update.getCallbackQuery().getData().split(";");
            if (callbackData[3].equals(CREATE_ROOM_LOCAL_STATE)) {
                String roomSettingsLocalState = callbackData[4]+";"+callbackData[5]+";"+callbackData[6];
                RoomSettings roomSettings = getRoomSettingsByLocalState(roomSettingsLocalState);
                Room room;
                try {
                    room = roomService.createRoomByChatIdAndRoomSettings(chatId,roomSettings);
                    state = chatStateService.setState(chatId, InRoomHandler.getStateForRoomCreator(room.getId()));
                    internalHandlerSwitcher.switchHandler(update,state);

                } catch (PlayerDoesNotExistException e) {
                    state = chatStateService.setState(chatId, RegistrationHandler.getStateForStartingRegistration());
                    internalHandlerSwitcher.switchHandler(update, state);
                }
            } else {
                String buttonsId = IdGenerator.generateForButton();
                String roomSettingsLocalState = callbackData[3]+";"+callbackData[4]+";"+callbackData[5];
                RoomSettings roomSettings = getRoomSettingsByLocalState(roomSettingsLocalState);
                EditMessageText settingsMessage = new EditMessageText();
                settingsMessage.setChatId(chatId);
                settingsMessage.setMessageId(messageId);
                settingsMessage.setText(roomSettings.toString());
                settingsMessage.setReplyMarkup(getSettingsMarkup(roomSettings, buttonsId));

                telegramBot.editMessage(settingsMessage);
                chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId));
            }
        }
    }

    private RoomSettings getInitialSettings() {
        return new RoomSettings(
                RoomSettings.AccessType.PUBLIC,
                RoomSettings.SpeedType.NORMAL,
                RoomSettings.VotingType.PUBLIC);
    }

    /**
     * Возвращает разметку с кнопками для настройки комнаты.
     * Каждая кнопка хранит информацию о том, какими должны быть настройки после нажатия
     * @param roomSettings
     * @param buttonsId
     * @return
     */
    private InlineKeyboardMarkup getSettingsMarkup(RoomSettings roomSettings, String buttonsId) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();

        var publicRoomButton = new InlineKeyboardButton();
        String publicRoomButtonLocalState = getLocalStateByRoomSettings(RoomSettings.AccessType.PUBLIC, roomSettings.getSpeedType(), roomSettings.getVotingType());
        publicRoomButton.setText(RoomSettings.AccessType.PUBLIC.getEmoji()+" "+RoomSettings.AccessType.PUBLIC.getType());
        publicRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId,publicRoomButtonLocalState));
        var privateRoomButton = new InlineKeyboardButton();
        String privateRoomButtonLocalState = getLocalStateByRoomSettings(RoomSettings.AccessType.PRIVATE, roomSettings.getSpeedType(), roomSettings.getVotingType());
        privateRoomButton.setText(RoomSettings.AccessType.PRIVATE.getEmoji()+" "+RoomSettings.AccessType.PRIVATE.getType());
        privateRoomButton.setCallbackData(StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId,privateRoomButtonLocalState));

        if (roomSettings.getAccessType().equals(RoomSettings.AccessType.PUBLIC))
            rowInLine1.add(privateRoomButton);
        else
            rowInLine1.add(publicRoomButton);

        var normalSpeedButton = new InlineKeyboardButton();
        String normalSpeedButtonLocalState = getLocalStateByRoomSettings(roomSettings.getAccessType(), RoomSettings.SpeedType.NORMAL, roomSettings.getVotingType());
        normalSpeedButton.setText(RoomSettings.SpeedType.NORMAL.getEmoji()+" "+RoomSettings.SpeedType.NORMAL.getType());
        normalSpeedButton.setCallbackData(StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId,normalSpeedButtonLocalState));
        var fastSpeedButton = new InlineKeyboardButton();
        String fastSpeedButtonLocalState = getLocalStateByRoomSettings(roomSettings.getAccessType(), RoomSettings.SpeedType.FAST, roomSettings.getVotingType());
        fastSpeedButton.setText(RoomSettings.SpeedType.FAST.getEmoji()+" "+RoomSettings.SpeedType.FAST.getType());
        fastSpeedButton.setCallbackData(StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId,fastSpeedButtonLocalState));

        if (roomSettings.getSpeedType().equals(RoomSettings.SpeedType.NORMAL))
            rowInLine1.add(fastSpeedButton);
        else
            rowInLine1.add(normalSpeedButton);

        var publicVotingButton = new InlineKeyboardButton();
        String publicVotingButtonLocalState = getLocalStateByRoomSettings(roomSettings.getAccessType(), roomSettings.getSpeedType(), RoomSettings.VotingType.PUBLIC);
        publicVotingButton.setText(RoomSettings.VotingType.PUBLIC.getEmoji()+" "+RoomSettings.VotingType.PUBLIC.getType());
        publicVotingButton.setCallbackData(StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId,publicVotingButtonLocalState));
        var secretVotingButton = new InlineKeyboardButton();
        String secretVotingButtonLocalState = getLocalStateByRoomSettings(roomSettings.getAccessType(), roomSettings.getSpeedType(), RoomSettings.VotingType.SECRET);
        secretVotingButton.setText(RoomSettings.VotingType.SECRET.getEmoji()+" "+RoomSettings.VotingType.SECRET.getType());
        secretVotingButton.setCallbackData(StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId,secretVotingButtonLocalState));

        if (roomSettings.getVotingType().equals(RoomSettings.VotingType.PUBLIC))
            rowInLine1.add(secretVotingButton);
        else
            rowInLine1.add(publicVotingButton);

        var createButton = new InlineKeyboardButton();
        String createButtonLocalState = getLocalStateByRoomSettings(roomSettings.getAccessType(), roomSettings.getSpeedType(), roomSettings.getVotingType());
        createButton.setText("Создать");
        createButton.setCallbackData(StateCreator.create(HANDLER_NAME,DO_NOT_SEND_SETTINGS_AGAIN_LOCAL_STATE,buttonsId,CREATE_ROOM_LOCAL_STATE,createButtonLocalState));

        rowInLine2.add(createButton);

        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);

        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
    }

    /**
     * Генерирует в текстовую строку хранящую настройки комнаты
     * @param accessType
     * @param speedType
     * @param votingType
     * @return
     */
    private String getLocalStateByRoomSettings(RoomSettings.AccessType accessType, RoomSettings.SpeedType speedType, RoomSettings.VotingType votingType) {
        String accessTypeStr;
        String speedTypeStr;
        String votingTypeStr;

        if (accessType.equals(RoomSettings.AccessType.PUBLIC)) {
            accessTypeStr = PUBLIC_ACCESS_SETTINGS;
        } else {
            accessTypeStr = PRIVATE_ACCESS_SETTINGS;
        }

        if (speedType.equals(RoomSettings.SpeedType.NORMAL)) {
            speedTypeStr = NORMAL_SPEED_SETTINGS;
        } else {
            speedTypeStr = FAST_SPEED_SETTINGS;
        }

        if (votingType.equals(RoomSettings.VotingType.PUBLIC)) {
            votingTypeStr = PUBLIC_VOTING_SETTINGS;
        } else {
            votingTypeStr = SECRET_VOTING_SETTINGS;
        }

        return StateCreator.create(accessTypeStr,speedTypeStr,votingTypeStr);
    }

    /**
     * Преобразует строку с настройками комнаты в объект RoomSettings
     * @param localState
     * @return
     */
    private RoomSettings getRoomSettingsByLocalState(String localState) {
        String[] stateData = localState.split(";");
        RoomSettings.AccessType accessType;
        RoomSettings.SpeedType speedType;
        RoomSettings.VotingType votingType;

        if (stateData[0].equals(PUBLIC_ACCESS_SETTINGS)) {
            accessType = RoomSettings.AccessType.PUBLIC;
        } else {
            accessType = RoomSettings.AccessType.PRIVATE;
        }

        if (stateData[1].equals(NORMAL_SPEED_SETTINGS)) {
            speedType = RoomSettings.SpeedType.NORMAL;
        } else {
            speedType = RoomSettings.SpeedType.FAST;
        }

        if (stateData[2].equals(PUBLIC_VOTING_SETTINGS)) {
            votingType = RoomSettings.VotingType.PUBLIC;
        } else {
            votingType = RoomSettings.VotingType.SECRET;
        }

        return new RoomSettings(accessType, speedType, votingType);
    }

    @Override
    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }

    public static String getStateForCreatingRoom() {
        return StateCreator.create(HANDLER_NAME, SpecialLocalState.EMPTY.name());
    }
}
