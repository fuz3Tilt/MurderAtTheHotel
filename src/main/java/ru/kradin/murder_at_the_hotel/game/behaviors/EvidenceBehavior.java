package ru.kradin.murder_at_the_hotel.game.behaviors;

import ru.kradin.murder_at_the_hotel.game.Evidence;
import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.types.EvidenceAffectType;
import ru.kradin.murder_at_the_hotel.game.affects.types.TeamAffectType;

import java.util.ArrayList;
import java.util.List;

public class EvidenceBehavior implements Behavior<EvidenceAffectType> {
    private List<Affect<EvidenceAffectType>> affects;
    private List<Evidence> evidences;
    private boolean leavesEvidences;

    public EvidenceBehavior() {
        affects = new ArrayList<>();
        evidences = new ArrayList<>();
        leavesEvidences = true;
    }

    @Override
    public void addAffect(Affect<EvidenceAffectType> affect) {
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
        evidences.clear();
    }
    public boolean leavesEvidences() {
        return leavesEvidences;
    }

    public void addEvidence(Evidence evidence) {
        boolean canAddEvidence = true;
        for (Affect<EvidenceAffectType> affect: affects) {
            if (affect.getAffectType().equals(EvidenceAffectType.DOES_NOT_STORE_EVIDENCE)) {
                canAddEvidence = false;
                evidences.clear();
            }
        }

        if (canAddEvidence)
            evidences.add(evidence);
    }

    public List<Evidence> getEvidences() {
        return evidences;
    }
}
