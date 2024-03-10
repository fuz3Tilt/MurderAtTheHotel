package ru.kradin.murder_at_the_hotel.game.abilities;

import org.junit.Test;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.Team;
import ru.kradin.murder_at_the_hotel.game.roles.NorthMafiaRole;
import ru.kradin.murder_at_the_hotel.game.roles.PeacefulRole;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CanJoinMafiaAbilityTest {
    @Test
    public void mafiaKillAbilityTest() {
        Gamer gamer1 = new Gamer(1,"1");
        gamer1.setRole(new NorthMafiaRole());

        Gamer gamer2 = new Gamer(2,"2");
        gamer2.setRole(new NorthMafiaRole());

        Gamer gamer3 = new Gamer(3,"3");
        gamer3.setRole(new PeacefulRole());
        PassiveAbility ability = (PassiveAbility) gamer3.getRole().getAbilities().get(0);
        ability.use(gamer3,null);

        List<Gamer> gamersTeam1 = new ArrayList<>();
        gamersTeam1.add(gamer1);
        gamersTeam1.add(gamer2);

        List<Gamer> gamersTeam2 = new ArrayList<>();
        gamersTeam2.add(gamer3);

        Team mafiaTeam1 = new Team(gamersTeam1);
        Team mafiaTeam2 = new Team(new ArrayList<>(gamersTeam1));
        Team peacefulTeam = new Team(gamersTeam2);

        gamer1.setViningTeam(mafiaTeam1);
        gamer1.setKnownTeam(mafiaTeam2);

        gamer2.setViningTeam(mafiaTeam1);
        gamer2.setKnownTeam(mafiaTeam2);

        gamer3.setViningTeam(peacefulTeam);
        gamer3.setKnownTeam(new Team(new ArrayList<>()));

        gamer3.changeTeam(gamer1);

        assertEquals(3, mafiaTeam1.getGamers().size());
        assertEquals(3, mafiaTeam2.getGamers().size());
        assertTrue(peacefulTeam.getGamers().isEmpty());
        assertTrue(gamer3.getRole() instanceof NorthMafiaRole);
    }
}