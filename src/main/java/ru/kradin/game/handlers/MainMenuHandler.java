package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.game.enums.SpecialLocalState;
import ru.kradin.game.keyboards.MainMenuKeyboard;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.TelegramBot;

@Component
public class MainMenuHandler implements InternalHandler{
    private static final String HANDLER_NAME = "Главное меню \uD83D\uDCF1";
    private TelegramBot telegramBot;
    @Autowired
    ChatStateService chatStateService;

    @Override
    public void handle(Update update, String data) {
        long chatId;
        if (update.hasMessage())
            chatId = update.getMessage().getChatId();
        else
            chatId = update.getCallbackQuery().getMessage().getChatId();

        String text = HANDLER_NAME;
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId),text);
        MainMenuKeyboard.setKeyboard(sendMessage);
        telegramBot.sendMessage(sendMessage);
    }

    @Override
    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }

    public static String getStateForEntering() {
        return HANDLER_NAME+";"+SpecialLocalState.NONE;
    }
}
