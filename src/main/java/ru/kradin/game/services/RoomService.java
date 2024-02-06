package ru.kradin.game.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kradin.game.exceptions.PlayerDoesNotExistException;
import ru.kradin.game.exceptions.PlayerIsNotInRoomException;
import ru.kradin.game.exceptions.RoomDoesNotExistException;
import ru.kradin.game.models.Player;
import ru.kradin.game.room.Room;
import ru.kradin.game.room.RoomFactory;
import ru.kradin.game.room.RoomType;
import ru.kradin.game.room.RoomsObserver;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomsObserver roomsObserver;
    @Autowired
    private RoomFactory roomFactory;
    @Autowired
    private PlayerService playerService;

    public Room createPublicRoomByChatId(long chatId) throws PlayerDoesNotExistException {
        Player player = playerService.getByChatId(chatId);
        return roomFactory.createRoom(player, RoomType.PUBLIC);
    }

    public Room createPrivateRoomByChatId(long chatId) throws PlayerDoesNotExistException {
        Player player = playerService.getByChatId(chatId);
        return roomFactory.createRoom(player, RoomType.PRIVATE);
    }

    public Room getRoomByChatId(long chatId) throws PlayerIsNotInRoomException {
        String roomId = roomsObserver.getChatIdRoomIdMap().get(chatId);

        if (roomId==null)
            throw new PlayerIsNotInRoomException();

        return roomsObserver.getRoomIdRoomMap().get(roomId);
    }

    public List<Room> getPublicRooms() {
        return roomsObserver.getPublicRooms();
    }

    public Room getRoomByRoomId(String roomId) throws RoomDoesNotExistException {
        Room room = roomsObserver.getRoomIdRoomMap().get(roomId);

        if (room==null)
            throw new RoomDoesNotExistException();

        return room;
    }
}
