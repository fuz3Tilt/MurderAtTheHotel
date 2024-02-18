package ru.kradin.game.enums;

public enum RoomType {
    PUBLIC("Открытая комната"),PRIVATE("Закрытая комната");
    private final String type;

    RoomType(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
