package ru.kradin.murder_at_the_hotel.services;

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
import ru.kradin.murder_at_the_hotel.configs.TelegramBotConfig;
import ru.kradin.murder_at_the_hotel.handlers.InternalHandlerSwitcher;
import ru.kradin.murder_at_the_hotel.handlers.InternalHandler;
import ru.kradin.murder_at_the_hotel.handlers.MenuCommand;

import java.util.*;

@Service
public class TelegramBot extends TelegramLongPollingBot {
    private final String username;
    private Map<String, MenuCommand> commandNameHandlerMap = new HashMap<>();
    private InternalHandlerSwitcher internalHandlerSwitcher;
    private ActionRateLimiter actionRateLimiter;

    public TelegramBot(TelegramBotConfig telegramBotConfig,
                       List<MenuCommand> menuCommands,
                       List<InternalHandler> internalHandlers,
                       InternalHandlerSwitcher internalHandlerSwitcher) {
        super(telegramBotConfig.getToken());
        username = telegramBotConfig.getUsername();

        actionRateLimiter = new ActionRateLimiter();

        internalHandlerSwitcher.init(internalHandlers,this);
        this.internalHandlerSwitcher = internalHandlerSwitcher;

        //добавляем команды во всплывающий список бота
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
        long chatId = 0;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }

        if (!actionRateLimiter.canActIfNotDoYourStuff(chatId)) {
            return;
        }

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
    private class ActionRateLimiter {
        private static final int MAX_ACTIONS_PER_SECOND = 3;
        private static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;
        private Map<Long, Long> chatIdLastMessageNanoSecondsTime;
        private Map<Long, Boolean> chatIdRateLimit;

        ActionRateLimiter() {
            chatIdLastMessageNanoSecondsTime = new HashMap<>();
            chatIdRateLimit = new HashMap<>();
        }

        public boolean canActIfNotDoYourStuff(long chatId) {
            long currentTime = System.nanoTime();
            long lastMessageTime = chatIdLastMessageNanoSecondsTime.getOrDefault(chatId, 0L);
            long timeInterval = NANOSECONDS_PER_SECOND / MAX_ACTIONS_PER_SECOND;

            if (currentTime - lastMessageTime >= timeInterval) {
                chatIdLastMessageNanoSecondsTime.put(chatId, currentTime);
                if (chatIdRateLimit.getOrDefault(chatId, false))
                    return false;
                else
                    return true;
            } else {
                if (!chatIdRateLimit.getOrDefault(chatId, false)) {
                    SendMessage limitReachedMessage = new SendMessage();
                    limitReachedMessage.setChatId(chatId);
                    limitReachedMessage.setText("Превышено количество действий в секунду. Ваши действия не будут обрабатываться в течении 3-х секунд.");
                    sendMessage(limitReachedMessage);

                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            SendMessage limitResetMessage = new SendMessage();
                            limitResetMessage.setChatId(chatId);
                            limitResetMessage.setText("Действия снова обрабатываются.");
                            sendMessage(limitResetMessage);
                            chatIdRateLimit.put(chatId, false);
                            timer.cancel();
                        }
                    };

                    chatIdRateLimit.put(chatId, true);

                    timer.schedule(timerTask, 1000*3);
                }
                return false;
            }
        }
    }
}
