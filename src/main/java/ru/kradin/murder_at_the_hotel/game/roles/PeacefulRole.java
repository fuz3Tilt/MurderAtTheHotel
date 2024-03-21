package ru.kradin.murder_at_the_hotel.game.roles;

import ru.kradin.murder_at_the_hotel.game.KnownTeam;
import ru.kradin.murder_at_the_hotel.game.ViningTeam;
import ru.kradin.murder_at_the_hotel.game.abilities.Ability;

import java.util.ArrayList;
import java.util.List;

public class PeacefulRole implements Role {
    private List<Ability> abilities;

    public PeacefulRole() {
        abilities = new ArrayList<>();
    }
    @Override
    public String getName() {
        return "Мирный";
    }

    @Override
    public String getDescription() {
        return "Выигрывает в команде мирных.";
    }

    @Override
    public ViningTeam getViningTeam() {
        return ViningTeam.PEACEFUL;
    }

    @Override
    public KnownTeam getKnownTeam() {
        return KnownTeam.NONE;
    }

    @Override
    public RoleColor getRoleColor() {
        return RoleColor.WHITE;
    }

    @Override
    public List<Ability> getAbilities() {
        return abilities;
    }
}
