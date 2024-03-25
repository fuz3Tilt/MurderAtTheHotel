package ru.kradin.murder_at_the_hotel.enums;

public enum GameStage {
    INTRODUCTION("Ознакомление"),FIRST_DISCUSSION("Первое обсуждение"),FIRST_VOTING("Первое голосование"),EXTRA_FIRST_VOTING("Первое дополнительное голосование"),NIGHT("Ночь"),DISCUSSION("Обсуждение"),VOTING("Голосование"),GAME_ENDED("Конец игры");
    private final String stage;

    GameStage(String stage) {
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }
}
