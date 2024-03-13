package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.roles.NorthMafiaRole;
import ru.kradin.murder_at_the_hotel.game.roles.PeacefulRole;
import ru.kradin.murder_at_the_hotel.game.roles.Role;

import java.util.ArrayList;
import java.util.List;

public class BlackDollarItem implements Item {
    private boolean charge;
    private boolean broken;

    public BlackDollarItem() {
        charge = true;
        broken = true;
    }

    @Override
    public String getName() {
        return "Чёрный доллар";
    }

    @Override
    public String getDescription() {
        return "Добавляет в семью мафии.";
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
                    Gamer targetCreator = target.getTargetCreator();
                    if (targetCreator.getRole() instanceof PeacefulRole) {
                        targetCreator.changeRole(new NorthMafiaRole());
                    }
                    active = false;
                }
            }
        };
        return abilityPerformer;
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
}
