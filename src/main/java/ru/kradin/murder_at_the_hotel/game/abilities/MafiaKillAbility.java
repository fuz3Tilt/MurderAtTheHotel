package ru.kradin.murder_at_the_hotel.game.abilities;

import ru.kradin.murder_at_the_hotel.game.Evidence;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.KillType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MafiaKillAbility implements ActiveAbility {
    private boolean charge;

    public MafiaKillAbility() {
        updateCharge();
    }

    @Override
    public String getName() {
        return "Убийство";
    }

    @Override
    public String getDescription() {
        return "Убивает выбранного игрока.";
    }

    @Override
    public boolean hasCharge() {
        return charge;
    }

    @Override
    public void updateCharge() {
        Random random = new Random();
        charge = random.nextBoolean();
    }

    @Override
    public AbilityPerformer getAbilityPerformer(Target target) {
        AbilityPerformer abilityPerformer = new AbilityPerformer() {
            private boolean active = true;
            @Override
            public void perform() {
                if (active) {
                    Gamer gamer = target.getTargets().get(0);
                    gamer.kill(target.getTargetCreator(), KillType.GUN_KILL);

                    if (gamer.leavesEvidences())
                        target.getTargetCreator().addEvidences(new Evidence(gamer));

                    if (target.getTargetCreator().leavesEvidences())
                        gamer.addEvidences(new Evidence(target.getTargetCreator()));
                }
                active = false;
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
