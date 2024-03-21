package ru.kradin.murder_at_the_hotel.game.roles;

import ru.kradin.murder_at_the_hotel.game.KnownTeam;
import ru.kradin.murder_at_the_hotel.game.ViningTeam;
import ru.kradin.murder_at_the_hotel.game.abilities.Ability;

import java.util.List;

public interface Role {
    String getName();
    String getDescription();
    ViningTeam getViningTeam();
    KnownTeam getKnownTeam();
    RoleColor getRoleColor();
    List<Ability> getAbilities();
}
