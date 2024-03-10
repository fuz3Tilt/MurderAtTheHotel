package ru.kradin.murder_at_the_hotel.game.behaviors;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.types.NightActionAffectType;

import java.util.ArrayList;
import java.util.List;

public class NightActionsBehavior implements Behavior<NightActionAffectType> {
    private List<Affect<NightActionAffectType>> affects;
    private boolean actAtNight;

    public NightActionsBehavior() {
        affects = new ArrayList<>();
        actAtNight = true;
    }

    @Override
    public void addAffect(Affect<NightActionAffectType> affect) {
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

    public boolean canActAtNight() {
        return actAtNight;
    }
}
