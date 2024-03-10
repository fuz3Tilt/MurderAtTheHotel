package ru.kradin.murder_at_the_hotel.game.roles;

import ru.kradin.murder_at_the_hotel.game.KnownTeam;
import ru.kradin.murder_at_the_hotel.game.ViningTeam;
import ru.kradin.murder_at_the_hotel.game.abilities.Ability;
import ru.kradin.murder_at_the_hotel.game.abilities.CherifCheckAbility;

import java.util.ArrayList;
import java.util.List;

public class SherifRole implements Role{
    private List<Ability> abilities;
    public SherifRole() {
        abilities = new ArrayList<>();
        abilities.add(new CherifCheckAbility());
    }
    @Override
    public String getName() {
        return "Шериф";
    }

    @Override
    public String getDescription() {
        return "Выигрывает в команде мирных. Знает всех федералов.";
    }

    @Override
    public ViningTeam getViningTeam() {
        return ViningTeam.PEACEFUL;
    }

    @Override
    public KnownTeam getKnownTeam() {
        return KnownTeam.FEDERALS;
    }

    @Override
    public List<Ability> getAbilities() {
        return null;
    }
}
