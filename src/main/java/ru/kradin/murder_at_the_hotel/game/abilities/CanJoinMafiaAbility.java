package ru.kradin.murder_at_the_hotel.game.abilities;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.CommonAffect;
import ru.kradin.murder_at_the_hotel.game.affects.types.TeamAffectType;

import java.util.List;

public class CanJoinMafiaAbility implements PassiveAbility{
    @Override
    public String getName() {
        return "Иуда";
    }

    @Override
    public String getDescription() {
        return "Даёт игроку возможность стать мафией.";
    }

    @Override
    public void use(Gamer abilityOwner, List<Gamer> gamers) {
        Affect<TeamAffectType> affect = new CommonAffect<>(TeamAffectType.CAN_JOIN_MAFIA, -1);
        abilityOwner.addAffect(affect);
    }
}
