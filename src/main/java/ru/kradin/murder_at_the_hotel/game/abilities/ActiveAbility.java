package ru.kradin.murder_at_the_hotel.game.abilities;

import ru.kradin.murder_at_the_hotel.game.Gamer;

import java.util.List;

public interface ActiveAbility extends Ability {
    @Override
    default boolean isActive() {
        return true;
    }
    public boolean hasCharge();
    public void updateCharge();
    public AbilityPerformer getAbilityPerformer(Target target);
    public int getTargetCount();
    public List<Gamer> getAvailableTargets(Gamer abilityUser, List<Gamer> allGamers);
}
