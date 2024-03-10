package ru.kradin.murder_at_the_hotel.game.roles;

import ru.kradin.murder_at_the_hotel.game.KnownTeam;
import ru.kradin.murder_at_the_hotel.game.ViningTeam;
import ru.kradin.murder_at_the_hotel.game.abilities.Ability;
import ru.kradin.murder_at_the_hotel.game.abilities.MafiaKillAbility;

import java.util.ArrayList;
import java.util.List;

public class NorthMafiaRole implements Role{
    private List<Ability> abilities;

    public NorthMafiaRole() {
        abilities = new ArrayList<>();
        abilities.add(new MafiaKillAbility());
    }

    @Override
    public String getName() {
        return "Северная мафия";
    }

    @Override
    public String getDescription() {
        return "Побеждает в команде северной мафии.";
    }

    @Override
    public ViningTeam getViningTeam() {
        return ViningTeam.NORTH_MAFIA;
    }

    @Override
    public KnownTeam getKnownTeam() {
        return KnownTeam.NORTH_MAFIA;
    }

    @Override
    public List<Ability> getAbilities() {
        return abilities;
    }
}
