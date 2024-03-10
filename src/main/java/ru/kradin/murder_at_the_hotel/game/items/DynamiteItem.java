package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Evidence;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.KillType;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.affects.CommonAffect;
import ru.kradin.murder_at_the_hotel.game.affects.types.EvidenceAffectType;

import java.util.ArrayList;
import java.util.List;

public class DynamiteItem implements Item {
    private boolean charge;
    private boolean broken;

    public DynamiteItem() {
        charge = true;
        broken = true;
    }

    @Override
    public String getName() {
        return "Динамит";
    }

    @Override
    public String getDescription() {
        return "Убивает игрока. Уничтожает все улики.";
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
                Gamer targetCreator = target.getTargetCreator();
                Gamer targetGamer = target.getTargets().get(0);

                targetGamer.kill(targetCreator, KillType.EXPLOSIVE_DEVICE);

                if (targetGamer.leavesEvidences())
                    targetCreator.addEvidences(new Evidence(targetGamer));

                targetGamer.addAffect(new CommonAffect<EvidenceAffectType>(EvidenceAffectType.DOES_NOT_STORE_EVIDENCE, 1));
                active = false;
            }
        };
        broken = true;
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
