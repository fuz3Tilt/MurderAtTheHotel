package ru.kradin.game.models;

import javax.persistence.*;

@Entity
public class ChatState {
    @Id
    private long chatId;
    /**
     * Хранит информацию в формате "имя_обработчика;данные_для_обработчика"
     */
    @Column(nullable = false)
    private String state;

    public ChatState() {
    }

    public ChatState(long chatId, String state) {
        this.chatId = chatId;
        this.state = state;
    }

    public long getChatId() {
        return chatId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
