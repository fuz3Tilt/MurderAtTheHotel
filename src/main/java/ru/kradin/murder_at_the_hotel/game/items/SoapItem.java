package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.affects.CommonAffect;
import ru.kradin.murder_at_the_hotel.game.affects.types.EvidenceAffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoapItem implements Item{
    private boolean charge;
    private boolean broken;

    public SoapItem() {
        charge = true;
        broken = false;
    }

    @Override
    public String getName() {
        return "Мыло";
    }

    @Override
    public String getDescription() {
        return "Скрывает улики на один ход. Ломается с шансом 50%.";
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
                    targetCreator.addAffect(new CommonAffect(EvidenceAffectType.LEAVES_NO_EVIDENCE, 1));
                    active = false;
                }
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
        availableTargets.add(abilityUser);
        return availableTargets;
    }
}
