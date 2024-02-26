package ru.kradin.murder_at_the_hotel.room;

import ru.kradin.murder_at_the_hotel.models.Player;

public class RoomFactory {
    private final RoomObserver roomObserver;

    public RoomFactory(RoomObserver roomObserver) {
        this.roomObserver = roomObserver;
    }

    public Room createRoom(Player owner, RoomSettings roomSettings) {
        return new Room(roomObserver, owner, roomSettings);
    }
}
