package ru.kradin.game.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kradin.game.configs.TelegramBotConfig;
import ru.kradin.game.handlers.InternalHandlerSwitcher;
import ru.kradin.game.handlers.InternalHandler;
import ru.kradin.game.handlers.MenuCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramBot extends TelegramLongPollingBot {
    private final String username;
    private Map<String, MenuCommand> commandNameHandlerMap = new HashMap<>();
    private InternalHandlerSwitcher internalHandlerSwitcher;

    public TelegramBot(TelegramBotConfig telegramBotConfig,
                       List<MenuCommand> menuCommands,
                       List<InternalHandler> internalHandlers,
                       InternalHandlerSwitcher internalHandlerSwitcher) {
        super(telegramBotConfig.getToken());
        username = telegramBotConfig.getUsername();

        internalHandlerSwitcher.init(internalHandlers,this);
        this.internalHandlerSwitcher = internalHandlerSwitcher;

        List<BotCommand> listOfCommands = new ArrayList<>();
        menuCommands.stream().forEach(menuCommand -> {
            menuCommand.setTelegramBot(this);
            commandNameHandlerMap.put(menuCommand.getCommand(), menuCommand);
            listOfCommands.add(new BotCommand(menuCommand.getCommand(),menuCommand.getDescription()));
        });
        try {
            this.execute(new DeleteMyCommands());
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            MenuCommand menuCommand = commandNameHandlerMap.get(messageText);
            if (menuCommand!=null) {
                menuCommand.handle(update);
            } else {
                internalHandlerSwitcher.switchHandler(update);
            }
        } else {
            internalHandlerSwitcher.switchHandler(update);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    public void sendMessage(SendMessage sendMessage) {
        sendMessage.setParseMode("HTML");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void editMessage(EditMessageText editMessageText) {
        editMessageText.setParseMode("HTML");
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
