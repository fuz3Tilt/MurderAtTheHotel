package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.GameSession;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.roles.PeacefulRole;
import ru.kradin.murder_at_the_hotel.game.roles.Role;

import java.util.ArrayList;
import java.util.List;

public class ConfessionItem implements Item {
    private boolean charge;
    private boolean broken;

    public ConfessionItem() {
        charge = true;
        broken = false;
    }

    @Override
    public String getName() {
        return "Исповедь";
    }

    @Override
    public String getDescription() {
        return "Показывает игрокам вашу роль.";
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
                    Gamer gamer = target.getTargetCreator();
                    GameSession gameSession= target.getGameSession();
                    gameSession.addMessageToPlayers("У "+gamer.getNickname()+" роль "+gamer.getRole().getName());
                    active = false;
                }
            }
        };
        broken = true;
        charge = false;
        return abilityPerformer;
    }

    @Override
    public int getTargetCount() {
        return 1;
    }

    @Override
    public List<Gamer> getAvailableTargets(Gamer abilityUser, List<Gamer> allGamers) {
        List<Gamer> availableTargets = new ArrayList<>();
        availableTargets.add(abilityUser);
        return availableTargets;
    }

    @Override
    public List<Class<? extends Role>> getRelatedRoles() {
        Class<? extends Role> role1 = PeacefulRole.class;
        List<Class<? extends Role>> relatedRoles = new ArrayList<>();
        relatedRoles.add(role1);
        return relatedRoles;
    }

    @Override
    public Item clone() {
        return new ConfessionItem();
    }
}
