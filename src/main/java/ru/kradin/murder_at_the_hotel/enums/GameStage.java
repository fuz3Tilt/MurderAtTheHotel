package ru.kradin.murder_at_the_hotel.enums;

public enum GameStage {
    INTRODUCTION("Ознакомление"),FIRST_DISCUSSION("Первое обсуждение"),FIRST_VOTING("Первое голосование"),EXTRA_FIRST_VOTING("Дополнительное голосование"),NIGHT("Ночь"),DISCUSSION("Обсуждение"),VOTING("Голосование"),EXTRA_VOTING_STEP_1("Дополнительное голосование"),EXTRA_VOTING_STEP_2("Дополнительное голосование"),GAME_ENDED("Конец игры");
    private final String stage;

    GameStage(String stage) {
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }
}
