package ru.kradin.murder_at_the_hotel.game.items;

import ru.kradin.murder_at_the_hotel.game.Evidence;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.roles.Role;
import ru.kradin.murder_at_the_hotel.game.roles.SherifRole;

import java.util.ArrayList;
import java.util.List;

public class InspectionItem implements Item {
    private boolean charge;
    private boolean broken;

    public InspectionItem() {
        charge = true;
        broken= false;
    }

    @Override
    public String getName() {
        return "Досмотр";
    }

    @Override
    public String getDescription() {
        return "Вскрывает багаж игрока.";
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
                    Gamer targetGamer = target.getTargets().get(0);

                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append("У игрока ").append(targetGamer.getNickname()).append(" найдены предметы:\n");
                    int itemCount = 1;
                    for (Item item : targetGamer.getBag().getItems()) {
                        messageBuilder.append(itemCount).append(") ").append(item.getName()).append("\n");
                        itemCount++;
                    }
                    String message = messageBuilder.toString();
                    targetCreator.getMessagesToPlayer().add(message);

                    if (targetCreator.leavesEvidences())
                        targetGamer.addEvidences(new Evidence(targetCreator));

                    if (targetGamer.leavesEvidences())
                        targetCreator.addEvidences(new Evidence(targetGamer));

                    active = false;
                }
            }
        };
        charge = false;
        broken = true;
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
        Class<? extends Role> role1 = SherifRole.class;
        List<Class<? extends Role>> relatedRoles = new ArrayList<>();
        relatedRoles.add(role1);
        return relatedRoles;
    }

    @Override
    public Item clone() {
        return new InspectionItem();
    }
}
