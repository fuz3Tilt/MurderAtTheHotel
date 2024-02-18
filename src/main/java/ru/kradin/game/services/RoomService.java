package ru.kradin.game.services;

import org.springframework.stereotype.Service;
import ru.kradin.game.exceptions.PlayerDoesNotExistException;
import ru.kradin.game.exceptions.RoomDoesNotExistException;
import ru.kradin.game.models.Player;
import ru.kradin.game.room.Room;
import ru.kradin.game.room.RoomFactory;
import ru.kradin.game.enums.RoomType;
import ru.kradin.game.room.RoomsObserver;

import java.util.List;

@Service
public class RoomService {
    private RoomsObserver roomsObserver;
    private PlayerService playerService;
    private RoomFactory roomFactory;

    public RoomService(RoomsObserver roomsObserver, PlayerService playerService) {
        this.roomsObserver = roomsObserver;
        this.playerService = playerService;
        this.roomFactory = new RoomFactory(roomsObserver);
    }

    public Room createRoomByChatIdAndRoomType(long chatId, RoomType roomType) throws PlayerDoesNotExistException {
        Player player = playerService.getByChatId(chatId);
        return roomFactory.createRoom(player, roomType);
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

    public Room joinRoomByRoomIdAndPlayerChatId(String roomId, long chatId) throws RoomDoesNotExistException, PlayerDoesNotExistException {
        Player player = playerService.getByChatId(chatId);
        Room room = roomsObserver.getRoomIdRoomMap().get(roomId);

        if (room==null)
            throw new RoomDoesNotExistException();

        room.addPlayer(player);

        return room;
    }
}
