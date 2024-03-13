package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Evidence;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.KillType;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.roles.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShotgunItem implements Item {
    private boolean charge;
    private boolean broken;

    public ShotgunItem() {
        charge = true;
        broken = false;
    }

    @Override
    public String getName() {
        return "Дробовик";
    }

    @Override
    public String getDescription() {
        return "Убивает игрока. Ломается с 50% шансом.";
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
        Random random = new Random();
        broken = random.nextBoolean();
        charge = false;
        return abilityPerformer;
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
        return new ArrayList<>();
    }

    @Override
    public Item clone() {
        return new ShotgunItem();
    }
}
