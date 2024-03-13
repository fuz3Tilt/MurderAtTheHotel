package ru.kradin.murder_at_the_hotel.services;

import org.springframework.stereotype.Service;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.ViningTeam;
import ru.kradin.murder_at_the_hotel.game.roles.NorthMafiaRole;
import ru.kradin.murder_at_the_hotel.game.roles.PeacefulRole;
import ru.kradin.murder_at_the_hotel.game.roles.SherifRole;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class RoleAssignerService {

    public void assignRoles(List<Gamer> gamers) {
        assignRandomRoles(gamers);
        while (!hasManyViningTeams(gamers)) {
            assignRandomRoles(gamers);
        }
    }

    private boolean hasManyViningTeams(List<Gamer> gamers) {
        Set<ViningTeam> viningTeams = new HashSet<>();

        for (Gamer gamer : gamers) {
            viningTeams.add(gamer.getRole().getViningTeam());
        }

        return viningTeams.size() >= 2;
    }

    private void assignRandomRoles(List<Gamer> gamers) {
        for (Gamer gamer: gamers) {
            assignRandomRole(gamer);
        }
    }

    private void assignRandomRole(Gamer gamer) {
            Random random = new Random();
            int roleIndex = random.nextInt(3) + 1;

            if (roleIndex == 1) {
                gamer.setRole(new NorthMafiaRole());
            }

            if (roleIndex == 2) {
                gamer.setRole(new PeacefulRole());
            }

            if (roleIndex == 3) {
                gamer.setRole(new SherifRole());
            }
    }
}
