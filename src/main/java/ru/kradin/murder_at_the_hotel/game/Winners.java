package ru.kradin.murder_at_the_hotel.game;

import java.util.List;

public class Winners {
    private ViningTeam viningTeam;
    private List<Gamer> gamers;

    public Winners(ViningTeam viningTeam, List<Gamer> gamers) {
        this.viningTeam = viningTeam;
        this.gamers = gamers;
    }

    public ViningTeam getViningTeam() {
        return viningTeam;
    }

    public List<Gamer> getGamers() {
        return gamers;
    }

    public boolean hasWinners() {
        return viningTeam != null;
    }
}
