package ru.kradin.murder_at_the_hotel.game.abilities;

import ru.kradin.murder_at_the_hotel.game.Gamer;

import java.util.List;

public interface PassiveAbility extends Ability {
    @Override
    default boolean isActive() {
        return false;
    }

    public void use(Gamer abilityUser, List<Gamer> allGamers);
}
