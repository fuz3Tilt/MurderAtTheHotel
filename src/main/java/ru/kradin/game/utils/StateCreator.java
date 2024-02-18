package ru.kradin.game.utils;

public class StateCreator {
    public static String create(String... strings) {
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            state.append(strings[i]);
            if (i != strings.length - 1) {
                state.append(";");
            }
        }
        return state.toString();
    }
}
