package ru.kradin.game.keyboards;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MainMenuKeyboard {
    public static void setKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();

        row1.add("Открытые комнаты \uD83C\uDFAE");
        keyboardRows.add(row1);

        row2.add("Создать открытую комнату \uD83D\uDD13");
        row2.add("Создать закрытую комнату \uD83D\uDD12");
        keyboardRows.add(row2);

        row3.add("Присоединиться по id \uD83D\uDD10");
        keyboardRows.add(row3);

        row4.add("Сменит никнейм ⚙\uFE0F");
        keyboardRows.add(row4);

        keyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setReplyMarkup(keyboardMarkup);
    }
}
