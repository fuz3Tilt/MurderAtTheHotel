package ru.kradin.murder_at_the_hotel.game.behaviors;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.types.CommunicationAffectType;

import java.util.ArrayList;
import java.util.List;

public class CommunicationBehavior implements Behavior<CommunicationAffectType> {
    private boolean canCommunicate;
    private ParticipantType participantType;
    private List<Affect<CommunicationAffectType>> affects;

    public CommunicationBehavior() {
        affects = new ArrayList<>();
        canCommunicate = true;
        participantType = ParticipantType.ALIVE;
    }

    @Override
    public void addAffect(Affect<CommunicationAffectType> affect) {
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

    public boolean canCommunicate() {
        return canCommunicate;
    }

    public ParticipantType getParticipantType() {
        return participantType;
    }
}
