package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.PlayerService;
import ru.kradin.game.services.TelegramBot;
import ru.kradin.game.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegistrationHandler implements InternalHandler {
    private static final String HANDLER_NAME = "registration";
    private static final String ENTER_NICKNAME = "enter_nickname";
    private static final String SET_NICKNAME = "set_nickname";
    private static final String REGISTER = "register";
    private static final String YES = "Да ✅";
    private static final String NO = "Нет ❌";
    private TelegramBot telegramBot;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    PlayerService playerService;
    @Autowired
    private ChatStateService chatStateService;

    @Override
    public void handle(Update update, String state) {
        String[] data = state.split(";");
        String methodName = data[1];
        switch (methodName) {
            case ENTER_NICKNAME:
                enterNickname(update);
                break;
            case SET_NICKNAME:
                setNickname(update);
                break;
            case REGISTER:
                register(update, state);
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

    private void enterNickname(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = "Чтобы пройти регистрацию, введите никнейм:";
        sandSimpleMessage(chatId,text);
        chatStateService.setState(chatId,HANDLER_NAME+";"+SET_NICKNAME);
    }

    private void setNickname(Update update) {
        long chatId = update.getMessage().getChatId();
        String nickname = update.getMessage().getText();
        if (playerService.isNicknameUses(nickname)) {
            String text = "Никнейм занят, попробуйте ещё раз:";
            sandSimpleMessage(chatId,text);
        } else {
            String buttonsId = IdGenerator.generateForButton();

            String text = "Вы уверены, что хотите никнейм: "+nickname+"?";
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId),text);

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();
            yesButton.setText(YES);
            yesButton.setCallbackData(HANDLER_NAME+";"+REGISTER+";"+buttonsId+";"+nickname+";"+YES);
            var noButton = new InlineKeyboardButton();
            noButton.setText(NO);
            noButton.setCallbackData(HANDLER_NAME+";"+REGISTER+";"+buttonsId+";"+nickname+";"+NO);

            rowInLine.add(yesButton);
            rowInLine.add(noButton);

            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            sendMessage.setReplyMarkup(markupInLine);

            telegramBot.sendMessage(sendMessage);
            chatStateService.setState(chatId,HANDLER_NAME+";"+REGISTER+";"+buttonsId);
        }
    }

    private void register(Update update, String state) {
        if (update.hasCallbackQuery()) {
            String[] stateData = state.split(";");
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = getMessageId(update.getCallbackQuery().getMessage());
            String[] callbackData = update.getCallbackQuery().getData().split(";");
            String pressedButton = callbackData[4];
            String nickname = callbackData[3];

            String stateButtonId = stateData[2];
            String callbackButtonId = callbackData[2];
            if (!stateButtonId.equals(callbackButtonId))
                return;

            if (pressedButton.equals(YES)) {
                playerService.register(chatId, nickname);
                String text = "Вы успешно зарегистрировались, ваш никнейм "+nickname+"!";
                sandSimpleEditMessage(chatId,messageId,text);
                state = chatStateService.setState(chatId, MainMenuHandler.getStateForEntering());
                internalHandlerSwitcher.switchHandler(update,state);
            } else if (pressedButton.equals(NO)) {
                String text = "Введите никнейм:";
                sandSimpleEditMessage(chatId, messageId, text);
                chatStateService.setState(chatId, HANDLER_NAME+";"+SET_NICKNAME);
            }
        }
    }

    private String getState(Update update) {
        long chatId = update.getMessage().getChatId();
        return chatStateService.getStateByChatId(chatId);
    }

    private int getMessageId(MaybeInaccessibleMessage message) {
        int index = message.toString().indexOf(",");
        int messageId = Integer.parseInt(message.toString().substring(18,index));
        return messageId;
    }

    private void sandSimpleMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId),text);
        telegramBot.sendMessage(sendMessage);
    }

    private void sandSimpleEditMessage(long chatId, int messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        telegramBot.editMessage(editMessageText);
    }

    public static String getStateForEntering() {
        return HANDLER_NAME+";"+ENTER_NICKNAME;
    }
}
