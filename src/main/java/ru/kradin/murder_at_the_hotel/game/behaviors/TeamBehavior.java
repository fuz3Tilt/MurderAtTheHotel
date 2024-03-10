package ru.kradin.murder_at_the_hotel.game.behaviors;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.Team;
import ru.kradin.murder_at_the_hotel.game.ViningTeam;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.types.TeamAffectType;
import ru.kradin.murder_at_the_hotel.game.roles.NorthMafiaRole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeamBehavior implements Behavior<TeamAffectType> {
    private List<Affect<TeamAffectType>> affects;
    private Team viningTeam;
    private Team knownTeam;
    private Gamer owner;

    public TeamBehavior(Gamer owner) {
        this.owner = owner;
        affects = new ArrayList<>();
    }
    @Override
    public void addAffect(Affect<TeamAffectType> affect) {
        affects.add(affect);
    }

    @Override
    public void update() {
        for (Affect affect:affects) {
            affect.reduceDuration();
            if (!affect.isActive())
                affects.remove(affect);
        }
    }

    public Team getViningTeam() {
        return viningTeam;
    }

    public void setViningTeam(Team viningTeam) {
        this.viningTeam = viningTeam;
    }

    public Team getKnownTeam() {
        return knownTeam;
    }

    public void setKnownTeam(Team knownTeam) {
        this.knownTeam = knownTeam;
    }

    public void changeTeam(Gamer teamChanger) {
        switch (teamChanger.getRole().getViningTeam()) {
            case NORTH_MAFIA:
                Iterator<Affect<TeamAffectType>> iterator = affects.iterator();
                while (iterator.hasNext()) {
                    Affect<TeamAffectType> affect = iterator.next();
                    if (affect.getAffectType().equals(TeamAffectType.CAN_JOIN_MAFIA)) {
                        viningTeam.getGamers().remove(owner);
                        knownTeam.getGamers().remove(owner);
                        iterator.remove();
                        owner.setRole(new NorthMafiaRole());
                        Team newViningTeam = teamChanger.getViningTeam();
                        Team newKnownTeam = teamChanger.getKnownTeam();
                        newViningTeam.getGamers().add(owner);
                        newKnownTeam.getGamers().add(owner);
                        knownTeam = newKnownTeam;
                        this.viningTeam = newViningTeam;
                    }
                }
                break;
        }
    }
}
