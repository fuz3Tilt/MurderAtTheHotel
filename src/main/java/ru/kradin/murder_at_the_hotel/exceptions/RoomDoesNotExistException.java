package ru.kradin.murder_at_the_hotel.exceptions;

public class RoomDoesNotExistException extends Exception{
    private final static String MESSAGE = "Комната не существует.";
    public RoomDoesNotExistException() {
        super(MESSAGE);
    }
}
