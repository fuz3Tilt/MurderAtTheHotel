package ru.kradin.murder_at_the_hotel.enums;

public enum GameStage {
    INTRODUCTION("Ознакомление"),FIRST_DISCUSSION("Первое обсуждение"),FIRST_VOTING("Первое голосование"),NIGHT("Ночь"),DISCUSSION("Обсуждение"),VOTING("Голосование");
    private final String stage;

    GameStage(String stage) {
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }
}
