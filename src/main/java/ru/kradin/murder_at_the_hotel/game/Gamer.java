package ru.kradin.murder_at_the_hotel.game;

import ru.kradin.murder_at_the_hotel.game.abilities.AbilityPerformer;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;
import ru.kradin.murder_at_the_hotel.game.affects.types.EvidenceAffectType;
import ru.kradin.murder_at_the_hotel.game.behaviors.*;
import ru.kradin.murder_at_the_hotel.game.items.Bag;
import ru.kradin.murder_at_the_hotel.game.roles.Role;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Gamer {
    private long chatId;
    private String nickname;
    private boolean inGame;
    private Bag bag;
    private Role role;
    private EvidenceBehavior evidenceBehavior;
    private HealthBehavior healthBehavior;
    private PlanedActionsBehavior planedActionsBehavior;
    private Queue<String> messagesToPlayer;

    public Gamer(long chatId, String nickname) {
        this.chatId = chatId;
        this.nickname = nickname;
        messagesToPlayer = new LinkedList<>();
        evidenceBehavior = new EvidenceBehavior();
        healthBehavior = new HealthBehavior();
        planedActionsBehavior = new PlanedActionsBehavior();
        inGame = true;
    }

    public void addPlanedAction(AbilityPerformer abilityPerformer) {
        planedActionsBehavior.addAction(abilityPerformer);
    }
    public void performPlanedActions() {
        planedActionsBehavior.performAll();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    public boolean isAlive() {
        return healthBehavior.isAlive();
    }

    public boolean isCapable() {
        return healthBehavior.isCapable();
    }

    public void kill(Gamer killer, KillType killType) {
        healthBehavior.kill(killer,killType);
    }

    public void updateBehavior() {
        evidenceBehavior.update();
        healthBehavior.update();
        planedActionsBehavior.update();
    }
    public void addAffect(Affect affect) {
        if (affect.getAffectType() instanceof EvidenceAffectType) {
            evidenceBehavior.addAffect(affect);
        }
    }

    public void killByVoteDecision() {
        healthBehavior.killByVoteDecision();
    }

    public void changeRole(Role newRole) {
        role = newRole;
    }

    public boolean leavesEvidences() {
        return evidenceBehavior.leavesEvidences();
    }

    public void addEvidences(Evidence evidence) {
        evidenceBehavior.addEvidence(evidence);
    }

    public List<Evidence> getEvidences() {
        return evidenceBehavior.getEvidences();
    }

    public Queue<String> getMessagesToPlayer() {
        return messagesToPlayer;
    }

    public long getChatId() {
        return chatId;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
}
