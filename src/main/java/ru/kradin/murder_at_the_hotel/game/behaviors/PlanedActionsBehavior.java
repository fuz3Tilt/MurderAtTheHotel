package ru.kradin.murder_at_the_hotel.game.behaviors;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.types.PlanedActionsAffectType;

import java.util.ArrayList;
import java.util.List;

public class PlanedActionsBehavior implements Behavior<PlanedActionsAffectType> {
    private List<Affect<PlanedActionsAffectType>> affects;
    private List<AbilityPerformer> planedActions;

    public PlanedActionsBehavior() {
        affects = new ArrayList<>();
        planedActions = new ArrayList<>();
    }

    @Override
    public void addAffect(Affect<PlanedActionsAffectType> affect) {
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
        planedActions.clear();
    }

    public void addAction(AbilityPerformer abilityPerformer) {
        planedActions.add(abilityPerformer);
    }

    public void performAll() {
        for (AbilityPerformer abilityPerformer:planedActions) {
            abilityPerformer.perform();
        }
    }
}
