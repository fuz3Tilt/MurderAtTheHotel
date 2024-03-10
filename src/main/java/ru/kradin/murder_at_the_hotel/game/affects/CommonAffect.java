package ru.kradin.murder_at_the_hotel.game.affects;

public class CommonAffect<T extends Enum> implements Affect<T>{
    private T affectType;
    private int remainingDuration;

    public CommonAffect(T affectType, int remainingDuration) {
        this.affectType = affectType;
        this.remainingDuration = remainingDuration;
    }

    @Override
    public T getAffectType() {
        return affectType;
    }

    @Override
    public void reduceDuration() {
        if (remainingDuration > 0)
            remainingDuration--;
    }

    @Override
    public boolean isActive() {
        return remainingDuration!=0;
    }
}
