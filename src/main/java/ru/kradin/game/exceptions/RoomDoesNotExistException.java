package ru.kradin.game.exceptions;

public class RoomDoesNotExistException extends Exception{
    private final static String MESSAGE = "Комната не существует.";
    public RoomDoesNotExistException() {
        super(MESSAGE);
    }
}
