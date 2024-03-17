package ru.kradin.murder_at_the_hotel.keyboards;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.kradin.murder_at_the_hotel.handlers.MainMenuHandler;

import java.util.ArrayList;
import java.util.List;

public class MainMenuKeyboard {
    public static void setKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();

        row1.add(MainMenuHandler.getPublicRooms());
        keyboardRows.add(row1);

        row2.add(MainMenuHandler.getCreateRoom());
        row2.add(MainMenuHandler.getJoinById());
        keyboardRows.add(row2);

        row3.add(MainMenuHandler.getChangeNickname());
        keyboardRows.add(row3);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }
}
