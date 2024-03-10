package ru.kradin.murder_at_the_hotel.game.behaviors;

import ru.kradin.murder_at_the_hotel.game.Gamer;
import ru.kradin.murder_at_the_hotel.game.affects.Affect;

public interface Behavior<T extends Enum> {
    void addAffect(Affect<T> affect);
    void update();
}
