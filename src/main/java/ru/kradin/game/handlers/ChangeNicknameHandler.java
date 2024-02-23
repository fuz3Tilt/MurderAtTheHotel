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
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.PlayerService;
import ru.kradin.game.services.TelegramBot;
import ru.kradin.game.utils.IdGenerator;
import ru.kradin.game.utils.MessageIdUtil;
import ru.kradin.game.utils.NicknameValidator;
import ru.kradin.game.utils.StateCreator;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChangeNicknameHandler implements InternalHandler{
    private static final String HANDLER_NAME = "change_nickname";
    private static final String ENTER_NICKNAME = "e_n";
    private static final String SET_NICKNAME = "s_n";
    private static final String CONFIRM_NICKNAME = "c_n";
    private static final String BACK = "Назад ⬅\uFE0F";
    private static final String ENTER_NEW_NICKNAME = "Введите новый никнейм:";
    private static final String YES = "Да ✅";
    private static final String YES_BUTTON = "y";
    private static final String NO = "Нет ❌";
    private static final String NO_BUTTON = "n";
    private TelegramBot telegramBot;
    @Autowired
    private ChatStateService chatStateService;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    private PlayerService playerService;
    @Override
    public void handle(Update update, String state) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            String[] stateData = state.split(";");

            if (update.getMessage().getText().equals(BACK)) {
                state = chatStateService.setState(chatId,MainMenuHandler.getStateForGettingMainMenu());
                internalHandlerSwitcher.switchHandler(update,state);
                return;
            }
            switch (stateData[1]) {
                case ENTER_NICKNAME:
                    sendEnteringNicknameMessage(chatId);
                    chatStateService.setState(chatId,StateCreator.create(HANDLER_NAME,SET_NICKNAME));
                    break;
                case SET_NICKNAME:
                    String nickname = update.getMessage().getText();
                    sendConfirmMessageAndChangeState(chatId, nickname);
                    break;
            }
            // обработка нажатых кнопок
        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String[] callbackData = update.getCallbackQuery().getData().split(";");
            String[] stateData = state.split(";");
            if (callbackData[3].equals(YES_BUTTON)) {
                String nickname = stateData[3];

                playerService.changeNickname(chatId, nickname);

                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setMessageId(MessageIdUtil.getMessageId(update.getCallbackQuery().getMessage()));
                editMessage.setText("Вы успешно cменили никнейм на "+nickname+".");
                state = chatStateService.setState(chatId, MainMenuHandler.getStateForGettingMainMenu());
                telegramBot.editMessage(editMessage);
                internalHandlerSwitcher.switchHandler(update,state);
            } else {
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setMessageId(MessageIdUtil.getMessageId(update.getCallbackQuery().getMessage()));
                editMessage.setText(ENTER_NEW_NICKNAME);
                telegramBot.editMessage(editMessage);
                chatStateService.setState(chatId,StateCreator.create(HANDLER_NAME,SET_NICKNAME));
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

    public static String getStateForChangingNickname() {
        return StateCreator.create(HANDLER_NAME,ENTER_NICKNAME);
    }

    private void setBackKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();

        row1.add(BACK);
        keyboardRows.add(row1);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    private void sendEnteringNicknameMessage(long chatId) {
        SendMessage message = new SendMessage(String.valueOf(chatId),ENTER_NEW_NICKNAME);
        setBackKeyboard(message);
        telegramBot.sendMessage(message);
    }

    private void sendConfirmMessageAndChangeState(long chatId, String nickname) {
        nickname = NicknameValidator.eliminateUnnecessarySpaces(nickname);
        if (!NicknameValidator.isValid(nickname)) {
            SendMessage message = new SendMessage(String.valueOf(chatId), NicknameValidator.getValidationRules()+"\nПопробуйте ещё раз:");
            telegramBot.sendMessage(message);
            return;
        }

        if (playerService.isNicknameUses(nickname)) {
            SendMessage message = new SendMessage(String.valueOf(chatId), "Никнейм занят, попробуйте ещё раз:");
            telegramBot.sendMessage(message);
        } else {
            String buttonsId = IdGenerator.generateForButton();

            String text = "Вы уверены, что хотите никнейм: " + nickname + "?";
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();
            yesButton.setText(YES);
            yesButton.setCallbackData(StateCreator.create(HANDLER_NAME, CONFIRM_NICKNAME, buttonsId, YES_BUTTON));
            var noButton = new InlineKeyboardButton();
            noButton.setText(NO);
            noButton.setCallbackData(StateCreator.create(HANDLER_NAME, CONFIRM_NICKNAME, buttonsId, NO_BUTTON));

            rowInLine.add(yesButton);
            rowInLine.add(noButton);

            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            sendMessage.setReplyMarkup(markupInLine);

            telegramBot.sendMessage(sendMessage);
            chatStateService.setState(chatId, StateCreator.create(HANDLER_NAME, CONFIRM_NICKNAME, buttonsId, nickname));
        }
    }
}
