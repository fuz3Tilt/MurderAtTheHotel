package ru.kradin.game.handlers;

/**
 * Бины реализующие интерфейс попадут в меню команд бота.
 */
public interface MenuCommand {
    public String getCommand();
    public String getDescription();
}
