package ru.kradin.murder_at_the_hotel.services;

import org.springframework.stereotype.Service;
import ru.kradin.murder_at_the_hotel.exceptions.PlayerDoesNotExistException;
import ru.kradin.murder_at_the_hotel.exceptions.RoomDoesNotExistException;
import ru.kradin.murder_at_the_hotel.models.Player;
import ru.kradin.murder_at_the_hotel.room.Room;
import ru.kradin.murder_at_the_hotel.room.RoomFactory;
import ru.kradin.murder_at_the_hotel.room.RoomSettings;
import ru.kradin.murder_at_the_hotel.room.RoomsObserver;

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

    public Room createRoomByChatIdAndRoomSettings(long chatId, RoomSettings roomSettings) throws PlayerDoesNotExistException {
        Player player = playerService.getByChatId(chatId);
        return roomFactory.createRoom(player, roomSettings);
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
