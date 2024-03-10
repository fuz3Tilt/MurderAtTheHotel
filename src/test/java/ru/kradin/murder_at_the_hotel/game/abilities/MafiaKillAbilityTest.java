package ru.kradin.murder_at_the_hotel.game.abilities;

import org.junit.Test;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.roles.NorthMafiaRole;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MafiaKillAbilityTest {

    @Test
    public void mafiaKillAbilityTest() {
        Gamer gamer1 = new Gamer(1,"1");
        gamer1.setRole(new NorthMafiaRole());

        Gamer gamer2 = new Gamer(1,"1");

        List<Gamer> gamers = new ArrayList<>();
        gamers.add(gamer1);
        gamers.add(gamer2);

        assertTrue(gamer2.isAlive());

        ActiveAbility activeAbility = (ActiveAbility) gamer1.getRole().getAbilities().get(0);
        List<Gamer> targets = activeAbility.getAvailableTargets(gamer1, gamers);

        assertTrue(targets.get(0).equals(gamer2));

        gamer1.addPlanedAction(activeAbility.getAbilityPerformer(new Target(gamer1,targets)));
        gamer1.performPlanedActions();

        assertTrue(!gamer2.isAlive());
    }

}