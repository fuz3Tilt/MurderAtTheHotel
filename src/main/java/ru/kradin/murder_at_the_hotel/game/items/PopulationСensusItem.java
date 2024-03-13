package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.ViningTeam;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.roles.PeacefulRole;
import ru.kradin.murder_at_the_hotel.game.roles.Role;

import java.util.ArrayList;
import java.util.List;

public class PopulationСensusItem implements Item{
    private boolean charge;
    private boolean broken;

    public PopulationСensusItem() {
        charge = true;
        broken = false;
    }

    @Override
    public String getName() {
        return "Перепись населения";
    }

    @Override
    public String getDescription() {
        return "Показывает всех мирных игроков.";
    }

    @Override
    public boolean hasCharge() {
        return charge;
    }

    @Override
    public boolean isBroken() {
        return broken;
    }

    @Override
    public void updateCharge() {
    }

    @Override
    public AbilityPerformer getAbilityPerformer(Target target) {
        AbilityPerformer abilityPerformer = new AbilityPerformer() {
            private boolean active = true;
            @Override
            public void perform() {
                if (active) {
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append("Мирные игроки:\n");
                    int gamerCount = 1;
                    for (Gamer gamer:target.getTargets()) {
                        if (gamer.getRole().getViningTeam().equals(ViningTeam.PEACEFUL))
                            messageBuilder.append(gamerCount).append(") ").append(gamer.getNickname()).append("\n");
                    }
                    String message = messageBuilder.toString();
                    target.getTargetCreator().getMessagesToPlayer().add(message);
                    active = false;
                }
            }
        };
        charge = false;
        broken = true;
        return abilityPerformer;
    }

    @Override
    public List<Gamer> getAvailableTargets(Gamer abilityUser, List<Gamer> allGamers) {
        List<Gamer> availableTargets = new ArrayList<>();
        for (Gamer gamer:allGamers) {
            if (gamer.isAlive()) {
                availableTargets.add(gamer);
            }
        }
        return availableTargets;
    }

    @Override
    public List<Class<? extends Role>> getRelatedRoles() {
        Class<? extends Role> role1 = PeacefulRole.class;
        List<Class<? extends Role>> relatedRoles = new ArrayList<>();
        relatedRoles.add(role1);
        return relatedRoles;
    }
}
