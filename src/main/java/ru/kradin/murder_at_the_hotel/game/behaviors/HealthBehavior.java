package ru.kradin.murder_at_the_hotel.game.behaviors;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.KillType;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.types.HealthAffectType;

import java.util.ArrayList;
import java.util.List;

public class HealthBehavior implements Behavior<HealthAffectType> {
    private List<Affect<HealthAffectType>> affects;
    private boolean alive;

    public HealthBehavior() {
        affects = new ArrayList<>();
        alive = true;
    }

    @Override
    public void addAffect(Affect<HealthAffectType> affect) {
        affects.add(affect);
    }

    @Override
    public void update() {
        for (Affect affect: affects) {
            affect.reduceDuration();
            if (!affect.isActive()) {
                affects.remove(affect);
            }
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isCapable() {
        return true;
    }

    public void kill(Gamer killer, KillType killType) {
        alive = false;
    }

    public boolean canResurrect() {
        return false;
    }
    public void resurrect() {
        if (canResurrect())
        alive = true;
    }
}
