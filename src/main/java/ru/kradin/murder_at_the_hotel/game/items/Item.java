package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;

import java.util.List;

public interface Item {
    String getName();
    String getDescription();
    boolean hasCharge();
    boolean isBroken();
    void updateCharge();
    AbilityPerformer getAbilityPerformer(Target target);
    List<Gamer> getAvailableTargets(Gamer abilityUser, List<Gamer> allGamers);

}
