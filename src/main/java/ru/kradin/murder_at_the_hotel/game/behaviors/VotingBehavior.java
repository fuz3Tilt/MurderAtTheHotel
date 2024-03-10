package ru.kradin.murder_at_the_hotel.game.behaviors;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.types.VotingAffectType;

import java.util.ArrayList;
import java.util.List;

public class VotingBehavior implements Behavior<VotingAffectType> {
    private List<Affect<VotingAffectType>> affects;
    private boolean vote;

    public VotingBehavior() {
        affects = new ArrayList<>();
        vote = true;
    }

    @Override
    public void addAffect(Affect<VotingAffectType> affect) {
        affects.add(affect);
    }

    @Override
    public void update() {
        for (Affect affect: affects) {
            affect.reduceDuration();
            if (!affect.isActive()) {
                affects.remove(affect);
            }
        }
    }

    public boolean canVote() {
        return vote;
    }

    public int getVoteValue() {
        return 1;
    }
}
