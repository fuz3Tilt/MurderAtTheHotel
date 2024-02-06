package ru.kradin.game.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.game.services.TelegramBot;

public interface InternalHandler {
    public void handle(Update update, String data);
    public void setTelegramBot(TelegramBot telegramBot);
    public String getHandlerName();
}
