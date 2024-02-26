package ru.kradin.murder_at_the_hotel.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.murder_at_the_hotel.services.TelegramBot;

public interface InternalHandler {
    public void handle(Update update, String state);
    public void setTelegramBot(TelegramBot telegramBot);
    public String getHandlerName();
}
