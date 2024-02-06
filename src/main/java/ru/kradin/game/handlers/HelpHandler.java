package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.TelegramBot;

@Component
public class HelpHandler implements MessageHandler, MenuCommand{
    private static final String COMMAND = "/help";
    private static final String DESCRIPTION = "Помощь по игре";
    private TelegramBot telegramBot;

    @Override
    public void handle(Update update) {
        sendHelp(update);
    }

    @Override
    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    private void sendHelp(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = "Команда пока не работает.";
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId),text);
        telegramBot.sendMessage(sendMessage);
    }
}
