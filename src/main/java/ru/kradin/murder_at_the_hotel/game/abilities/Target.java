package ru.kradin.murder_at_the_hotel.game.abilities;

import ru.kradin.murder_at_the_hotel.game.GameSession;
import ru.kradin.murder_at_the_hotel.game.Gamer;

import java.util.List;

public class Target {
    private Gamer targetCreator;
    private List<Gamer> targets;
    private GameSession gameSession;

    public Target(Gamer targetCreator, List<Gamer> targets, GameSession gameSession) {
        this.targetCreator = targetCreator;
        this.targets = targets;
        this.gameSession = gameSession;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public Gamer getTargetCreator() {
        return targetCreator;
    }

    public List<Gamer> getTargets() {
        return targets;
    }
}
