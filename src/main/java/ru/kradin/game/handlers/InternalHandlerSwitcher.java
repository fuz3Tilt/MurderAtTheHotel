package ru.kradin.game.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kradin.game.services.ChatStateService;
import ru.kradin.game.services.TelegramBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InternalHandlerSwitcher {
    private Map<String,InternalHandler> nameHandlerMap = new HashMap<>();
    @Autowired
    ChatStateService chatStateService;

    public void init(List<InternalHandler> internalHandlers, TelegramBot telegramBot) {
        internalHandlers.stream().forEach(internalHandler -> {
            internalHandler.setTelegramBot(telegramBot);
            nameHandlerMap.put(internalHandler.getHandlerName(),internalHandler);
        });
    }

    public void switchHandler(Update update) {
        long chatId;
        String state;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            state = chatStateService.getStateByChatId(chatId);

            String[] callbackData = update.getCallbackQuery().getData().split(";");
            String callbackState = callbackData[0]+";"+callbackData[1];

            if (!state.equals(callbackState)) {
                return;
            }
        } else {
            chatId = update.getMessage().getChatId();
            state = chatStateService.getStateByChatId(chatId);
        }

        if (state.isEmpty())
            return;

        String[] handlerName_localState = state.split(";");
        String handlerName = handlerName_localState[0];
        String localState = handlerName_localState[1];
        InternalHandler internalHandler = nameHandlerMap.get(handlerName);
        internalHandler.handle(update,localState);
    }
    public void switchHandler(Update update, String state) {
        if (state.isEmpty())
            return;

        String[] handlerName_localState = state.split(";");
        String handlerName = handlerName_localState[0];
        String localState = handlerName_localState[1];
        InternalHandler internalHandler = nameHandlerMap.get(handlerName);
        internalHandler.handle(update,localState);
    }
}
