package ru.kradin.murder_at_the_hotel.game;

import java.util.List;

public class Team {
    private List<Gamer> gamers;

    public Team(List<Gamer> gamers) {
        this.gamers = gamers;
    }

    public List<Gamer> getGamers() {
        return gamers;
    }
}
