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
/**
 * Используется для смены обработчика
 */
public class InternalHandlerSwitcher {
    private Map<String,InternalHandler> nameHandlerMap = new HashMap<>();
    @Autowired
    private ChatStateService chatStateService;

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
            String[] stateData = state.split(";");
            String[] callbackData = update.getCallbackQuery().getData().split(";");

            if (stateData.length<3 || callbackData.length<3)
                return;

            String usefulStateData = stateData[0]+";"+stateData[1]+";"+stateData[2];
            String usefulCallbackData = callbackData[0]+";"+callbackData[1]+";"+callbackData[2];
            // проверяем, соответствует ли нажатая кнопка текущему состоянию
            if (!usefulStateData.equals(usefulCallbackData)) {
                return;
            }
        } else if (update.hasMessage()){
            chatId = update.getMessage().getChatId();
            state = chatStateService.getStateByChatId(chatId);
        } else {
            return;
        }

        performSwitch(update, state);
    }
    public void switchHandler(Update update, String state) {
        performSwitch(update, state);
    }

    private void performSwitch(Update update, String state) {
            String[] data = state.split(";");
            String handlerName = data[0];
            nameHandlerMap.get(handlerName).handle(update, state);
    }
}
