package ru.kradin.game.room;

import org.springframework.stereotype.Component;
import ru.kradin.game.enums.RoomNotifyType;

import java.util.*;

@Component
public class RoomsObserver {
    private Map<String, Room> roomIdRoomMap = new HashMap<>();
    private List<Room> publicRooms = new ArrayList<>();

    void notify(Room room, RoomNotifyType roomNotifyType) {
        String roomId = room.getId();
        switch (roomNotifyType) {
            case ROOM_CREATED:
                roomIdRoomMap.put(roomId, room);
                if (room.isPublic())
                    publicRooms.add(room);
                break;
            case PLAYER_LEFT:
                if (room.getPlayers().size() == 0) {
                    roomIdRoomMap.remove(roomId);

                    if (room.isPublic())
                        publicRooms.remove(room);
                }
                break;
        }
    }

    boolean isRoomIdInUse(String roomId) {
        Set<String> roomIdsSet = roomIdRoomMap.keySet();
        return roomIdsSet.contains(roomId);
    }

    public Map<String, Room> getRoomIdRoomMap() {
        return roomIdRoomMap;
    }

    public List<Room> getPublicRooms() {
        return publicRooms;
    }
}
