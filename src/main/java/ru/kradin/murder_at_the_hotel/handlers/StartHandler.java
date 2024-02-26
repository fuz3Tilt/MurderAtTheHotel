package ru.kradin.murder_at_the_hotel.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.murder_at_the_hotel.exceptions.PlayerDoesNotExistException;
import ru.kradin.murder_at_the_hotel.services.ChatStateService;
import ru.kradin.murder_at_the_hotel.services.PlayerService;
import ru.kradin.murder_at_the_hotel.services.TelegramBot;

@Component
public class StartHandler implements MenuCommand {
    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Запуск бота";
    private TelegramBot telegramBot;
    @Autowired
    private ChatStateService chatStateService;
    @Autowired
    private InternalHandlerSwitcher internalHandlerSwitcher;
    @Autowired
    private PlayerService playerService;

    @Override
    public void handle(Update update) {
        sendHello(update);
        changeStateIfNotRegistered(update);
        String state = getState(update);
        internalHandlerSwitcher.switchHandler(update, state);
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

    private void sendHello(Update update) {
        long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getChat().getFirstName();
        String text = "Привет, "+firstName+"!";
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId),text);
        telegramBot.sendMessage(sendMessage);
    }

    private String getState(Update update) {
        long chatId = update.getMessage().getChatId();
        return chatStateService.getStateByChatId(chatId);
    }

    private void changeStateIfNotRegistered(Update update) {
        long chatId = update.getMessage().getChatId();
        try {
            playerService.getByChatId(chatId);
        } catch (PlayerDoesNotExistException e) {
            chatStateService.setState(chatId,RegistrationHandler.getStateForStartingRegistration());
        }
    }
}
