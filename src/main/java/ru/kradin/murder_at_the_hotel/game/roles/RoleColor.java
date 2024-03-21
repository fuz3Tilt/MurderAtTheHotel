package ru.kradin.murder_at_the_hotel.game.roles;

public enum RoleColor {
    WHITE("белый"),BLACK("чёрный"),RED("красный");
    private final String color;
    RoleColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
