package ru.kradin.murder_at_the_hotel.room;

import ru.kradin.murder_at_the_hotel.models.Player;

public class RoomFactory {
    private final RoomsObserver roomsObserver;

    public RoomFactory(RoomsObserver roomsObserver) {
        this.roomsObserver = roomsObserver;
    }

    public Room createRoom(Player owner, RoomSettings roomSettings) {
        return new Room(roomsObserver, owner, roomSettings);
    }
}
