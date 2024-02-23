package ru.kradin.game.utils;

import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;

public class MessageIdUtil {
    /**
     * Возвращает id из объекта MaybeInaccessibleMessage в CallbackQuery
     * @param message
     * @return
     */
    public static int getMessageId(MaybeInaccessibleMessage message) {
        int index = message.toString().indexOf(",");
        int messageId = Integer.parseInt(message.toString().substring(18,index));
        return messageId;
    }
}
