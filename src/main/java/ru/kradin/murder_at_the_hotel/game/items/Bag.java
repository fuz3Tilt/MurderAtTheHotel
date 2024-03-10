package ru.kradin.murder_at_the_hotel.game.items;

import java.util.ArrayList;
import java.util.List;

public class Bag {
    private List<Item> items;

    public Bag() {
        items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }
}
