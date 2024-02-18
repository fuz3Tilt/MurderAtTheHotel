package ru.kradin.game.room;

import ru.kradin.game.enums.RoomType;
import ru.kradin.game.models.Player;

public class RoomFactory {
    private final RoomsObserver roomsObserver;

    public RoomFactory(RoomsObserver roomsObserver) {
        this.roomsObserver = roomsObserver;
    }

    public Room createRoom(Player owner, RoomType roomType) {
        return new Room(roomsObserver, owner, roomType);
    }
}
