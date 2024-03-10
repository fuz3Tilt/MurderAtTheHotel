package ru.kradin.murder_at_the_hotel.game;

import org.junit.Test;
import ru.kradin.murder_at_the_hotel.game.abilities.ActiveAbility;
import ru.kradin.murder_at_the_hotel.game.abilities.Target;
import ru.kradin.murder_at_the_hotel.game.roles.NorthMafiaRole;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GamerTest {
    @Test
    public void evidencesTest() {
        Gamer gamer1 = new Gamer(1,"1");
        gamer1.setRole(new NorthMafiaRole());

        Gamer gamer2 = new Gamer(1,"1");

        List<Gamer> gamers = new ArrayList<>();
        gamers.add(gamer1);
        gamers.add(gamer2);

        ActiveAbility activeAbility = (ActiveAbility) gamer1.getRole().getAbilities().get(0);
        List<Gamer> targets = activeAbility.getAvailableTargets(gamer1, gamers);

        gamer1.addPlanedAction(activeAbility.getAbilityPerformer(new Target(gamer1,targets)));
        gamer1.performPlanedActions();

        assertTrue(gamer1.getEvidences().size() == 1);
        assertTrue(gamer2.getEvidences().size() == 1);
        assertTrue(gamer1.getEvidences().get(0).getGamer().equals(gamer2));
        assertTrue(gamer2.getEvidences().get(0).getGamer().equals(gamer1));

        gamer1.updateBehavior();
        gamer2.updateBehavior();

        assertTrue(gamer1.getEvidences().isEmpty());
        assertTrue(gamer2.getEvidences().isEmpty());
    }
}