package ru.kradin.game.room;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoomsObserver {
    private Map<String, Room> roomIdRoomMap = new HashMap<>();
    private Map<Long, String> chatIdRoomIdMap = new HashMap<>();
    private List<Room> publicRooms = new ArrayList<>();

    void roomCreated(Room room) {
        String roomId = room.getId();
        long ownerChatId = room.getOwner().getChatId();
        roomIdRoomMap.put(roomId, room);
        chatIdRoomIdMap.put(ownerChatId, roomId);

        if (room.isPublic())
            publicRooms.add(room);
    }
    void playerAdded(long playerChatId, String roomId) {
        chatIdRoomIdMap.put(playerChatId, roomId);
        updateAndNotifyPlayers(roomId);
    }

    void playerRemoved(long playerChatId, String roomId) {
        chatIdRoomIdMap.remove(playerChatId);
        updateAndNotifyPlayers(roomId);
    }

    boolean isRoomIdInUse(String roomId) {
        Set<String> roomIdsSet = roomIdRoomMap.keySet();
        return roomIdsSet.contains(roomId);
    }

    /**
     * Возвращает копию Map
     * @return
     */
    public Map<String, Room> getRoomIdRoomMap() {
        return new HashMap<>(roomIdRoomMap);
    }

    /**
     * Возвращает копию Map
     * @return
     */
    public Map<Long, String> getChatIdRoomIdMap() {
        return new HashMap<>(chatIdRoomIdMap);
    }

    /**
     * Возвращает копию List
     * @return
     */
    public List<Room> getPublicRooms() {
        return new ArrayList<>(publicRooms);
    }

    private void updateAndNotifyPlayers(String roomId) {
        Room room = roomIdRoomMap.get(roomId);
        if (room.getPlayers().size() == 0) {
            roomIdRoomMap.remove(roomId);

            if (room.isPublic())
                publicRooms.remove(room);
        } else {
            //code
        }
    }
}
