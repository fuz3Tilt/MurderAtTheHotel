package ru.kradin.murder_at_the_hotel.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.murder_at_the_hotel.services.TelegramBot;

/**
 * Бины реализующие интерфейс попадут в меню команд бота.
 */
public interface MenuCommand {
    public void handle(Update update);
    public void setTelegramBot(TelegramBot telegramBot);
    public String getCommand();
    public String getDescription();
}
