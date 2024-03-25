package ru.kradin.murder_at_the_hotel.game;

public enum ViningTeam {
    PEACEFUL("Команда мирных"),NORTH_MAFIA("Команда северной мафии");

    private String name;

    ViningTeam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
