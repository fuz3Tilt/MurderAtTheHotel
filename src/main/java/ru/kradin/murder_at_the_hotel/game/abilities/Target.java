package ru.kradin.murder_at_the_hotel.game.abilities;

import ru.kradin.murder_at_the_hotel.game.Gamer;

import java.util.List;

public class Target {
    private Gamer targetCreator;
    private List<Gamer> targets;

    public Target(Gamer targetCreator, List<Gamer> targets) {
        this.targetCreator = targetCreator;
        this.targets = targets;
    }

    public Gamer getTargetCreator() {
        return targetCreator;
    }

    public List<Gamer> getTargets() {
        return targets;
    }
}
