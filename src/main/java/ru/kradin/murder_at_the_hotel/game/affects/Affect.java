package ru.kradin.murder_at_the_hotel.game.affects;

public interface Affect<T extends Enum> {
    T getAffectType();
    void reduceDuration();
    boolean isActive();
}
