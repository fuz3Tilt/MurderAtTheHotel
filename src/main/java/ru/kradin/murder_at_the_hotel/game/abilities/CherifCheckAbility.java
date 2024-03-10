package ru.kradin.murder_at_the_hotel.game.abilities;

import ru.kradin.murder_at_the_hotel.game.Evidence;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.utils.GameHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CherifCheckAbility implements ActiveAbility {
    private boolean charge;
    public CherifCheckAbility() {
        charge = true;
    }
    @Override
    public String getName() {
        return "Проверка шерифа";
    }

    @Override
    public String getDescription() {
        return "C 50% вероятностью раскрывает верную роль игрока.";
    }

    @Override
    public boolean hasCharge() {
        return charge;
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
                    Random random = new Random();
                    boolean rightPerform = random.nextBoolean();
                    Gamer targetCreator = target.getTargetCreator();
                    Gamer abilityTarget = target.getTargets().get(0);
                    String roleName = "";
                    if (rightPerform) {
                        roleName = abilityTarget.getRole().getName();
                    } else {
                        roleName = GameHelper.getRandomRole().getName();
                    }
                    targetCreator.getMessagesToPlayer().add("У игрока " + abilityTarget.getNickname() + " роль " + roleName + ".");

                    if (targetCreator.leavesEvidences())
                        abilityTarget.addEvidences(new Evidence(targetCreator));

                    if (abilityTarget.leavesEvidences())
                        targetCreator.addEvidences(new Evidence(abilityTarget));
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
