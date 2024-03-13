package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Evidence;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.KillType;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.roles.NorthMafiaRole;
import ru.kradin.murder_at_the_hotel.game.roles.Role;

import java.util.ArrayList;
import java.util.List;

public class ThompsonGunItem implements Item{
    private boolean charge;

    public ThompsonGunItem() {
        charge = true;
    }

    @Override
    public String getName() {
        return "Автомат томпсона";
    }

    @Override
    public String getDescription() {
        return "Убивает выбранного игрока пулей. Не расходуется, используется каждый ход.";
    }

    @Override
    public boolean hasCharge() {
        return charge;
    }

    @Override
    public boolean isBroken() {
        return false;
    }

    @Override
    public void updateCharge() {
        charge = true;
    }

    @Override
    public AbilityPerformer getAbilityPerformer(Target target) {
        AbilityPerformer abilityPerformer = new AbilityPerformer() {
            private boolean active = true;
            @Override
            public void perform() {
                if (active) {
                    Gamer killer = target.getTargetCreator();
                    Gamer toKill = target.getTargets().get(0);

                    toKill.kill(killer, KillType.GUN_KILL);

                    if (killer.leavesEvidences())
                        toKill.addEvidences(new Evidence(killer));

                    if (toKill.leavesEvidences())
                        killer.addEvidences(new Evidence(toKill));
                }
                active = false;
            }
        };
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
        for (Gamer gamer:allGamers) {
            if (!gamer.equals(abilityUser) && gamer.isAlive()) {
                availableTargets.add(gamer);
            }
        }
        return availableTargets;
    }

    @Override
    public List<Class<? extends Role>> getRelatedRoles() {
        Class<? extends Role> role1 = NorthMafiaRole.class;
        List<Class<? extends Role>> relatedRoles = new ArrayList<>();
        relatedRoles.add(role1);
        return relatedRoles;
    }

    @Override
    public Item clone() {
        return new ThompsonGunItem();
    }
}
