package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Evidence;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.KillType;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArmatureItem implements Item {
    private boolean charge;
    private boolean broken;

    public ArmatureItem() {
        charge = true;
        broken = false;
    }

    @Override
    public String getName() {
        return "Арматура";
    }

    @Override
    public String getDescription() {
        return "Убивает выбранного игрока. Ломается с 50% шансом.";
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
                    Gamer targetCreator = target.getTargetCreator();
                    Gamer targetGamer = target.getTargets().get(0);

                    targetGamer.kill(targetCreator, KillType.СOLD_WEAPON);

                    if (targetCreator.leavesEvidences())
                        targetGamer.addEvidences(new Evidence(targetCreator));

                    if (targetGamer.leavesEvidences())
                        targetCreator.addEvidences(new Evidence(targetGamer));

                    Random random = new Random();

                    broken = random.nextBoolean();
                }
            }
        };
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
}
