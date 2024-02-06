package ru.kradin.game.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.game.services.TelegramBot;

/**
 * Бины реализующие интерфейс добавятся в список обработчиков
 */
public interface MessageHandler {
    public void handle(Update update);
    public void setTelegramBot(TelegramBot telegramBot);
    public String getCommand();
}
