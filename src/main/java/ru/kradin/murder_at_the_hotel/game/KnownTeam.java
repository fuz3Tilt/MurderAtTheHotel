package ru.kradin.murder_at_the_hotel.game;

public enum KnownTeam {
    NONE(""),NORTH_MAFIA("северная мафия"),FEDERALS("федералы");
    private String name;
    KnownTeam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
