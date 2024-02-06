package ru.kradin.game.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kradin.game.models.Player;

@Component
public class RoomFactory {
    @Autowired
    private RoomsObserver roomsObserver;

    public Room createRoom(Player owner, RoomType roomType) {
        return new Room(roomsObserver, owner, roomType);
    }
}
